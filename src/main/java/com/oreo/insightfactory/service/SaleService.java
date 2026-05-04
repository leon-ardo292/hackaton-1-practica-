package com.oreo.insightfactory.service;

import com.oreo.insightfactory.dto.SaleRequest;
import com.oreo.insightfactory.dto.SaleResponse;
import com.oreo.insightfactory.handlerexception.ForbiddenOperationException;
import com.oreo.insightfactory.handlerexception.NotFoundException;
import com.oreo.insightfactory.model.AppUser;
import com.oreo.insightfactory.model.Sale;
import com.oreo.insightfactory.model.UserRole;
import com.oreo.insightfactory.repository.SaleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final CurrentUserService currentUserService;

    public SaleService(SaleRepository saleRepository, CurrentUserService currentUserService) {
        this.saleRepository = saleRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public SaleResponse create(SaleRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        assertBranchAccess(user, request.branch());
        Sale sale = new Sale(
                request.sku().trim(),
                request.units(),
                request.price(),
                request.branch().trim(),
                request.soldAt(),
                user.getUsername()
        );
        return SaleResponse.from(saleRepository.save(sale));
    }

    @Transactional(readOnly = true)
    public Page<SaleResponse> findAll(Instant from, Instant to, String branch, int page, int size) {
        AppUser user = currentUserService.getCurrentUser();
        String effectiveBranch = user.getRole() == UserRole.BRANCH ? user.getBranch() : branch;
        List<Sale> filtered = saleRepository.findAll()
                .stream()
                .filter(sale -> effectiveBranch == null || effectiveBranch.isBlank() || sale.getBranch().equalsIgnoreCase(effectiveBranch))
                .filter(sale -> from == null || !sale.getSoldAt().isBefore(from))
                .filter(sale -> to == null || !sale.getSoldAt().isAfter(to))
                .toList();

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int start = Math.min(safePage * safeSize, filtered.size());
        int end = Math.min(start + safeSize, filtered.size());
        List<SaleResponse> content = filtered.subList(start, end).stream().map(SaleResponse::from).toList();
        return new PageImpl<>(content, PageRequest.of(safePage, safeSize), filtered.size());
    }

    @Transactional(readOnly = true)
    public SaleResponse findById(Long id) {
        Sale sale = getVisibleSale(id);
        return SaleResponse.from(sale);
    }

    @Transactional
    public SaleResponse update(Long id, SaleRequest request) {
        Sale sale = getVisibleSale(id);
        AppUser user = currentUserService.getCurrentUser();
        assertBranchAccess(user, request.branch());
        sale.update(request.sku().trim(), request.units(), request.price(), request.branch().trim(), request.soldAt());
        return SaleResponse.from(sale);
    }

    @Transactional
    @PreAuthorize("hasRole('CENTRAL')")
    public void delete(Long id) {
        if (!saleRepository.existsById(id)) {
            throw new NotFoundException("Sale not found: " + id);
        }
        saleRepository.deleteById(id);
    }

    private Sale getVisibleSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sale not found: " + id));
        assertBranchAccess(currentUserService.getCurrentUser(), sale.getBranch());
        return sale;
    }

    private void assertBranchAccess(AppUser user, String branch) {
        if (user.getRole() == UserRole.BRANCH && !user.getBranch().equalsIgnoreCase(branch)) {
            throw new ForbiddenOperationException("BRANCH users can only access their assigned branch");
        }
    }
}
