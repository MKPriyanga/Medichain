package com.cts.delivery_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.delivery_service.dto.ProofOfReceiptRequestDto;
import com.cts.delivery_service.dto.ProofOfReceiptResponseDto;
import com.cts.delivery_service.entity.ProofOfReceipt;
import com.cts.delivery_service.exception.InvalidRequestException;
import com.cts.delivery_service.repository.ProofOfReceiptRepository;

@Service
@Transactional
public class ProofOfReceiptService {

    private final ProofOfReceiptRepository repository;

    public ProofOfReceiptService(ProofOfReceiptRepository repository) {
        this.repository = repository;
    }

    // CREATE PROOF
    public ProofOfReceiptResponseDto createProof(ProofOfReceiptRequestDto dto) {

        if (dto.getDeliveryId() == null) {
            throw new InvalidRequestException("Delivery ID is required");
        }

        if (dto.getDepartmentId() == null) {
            throw new InvalidRequestException("Department ID is required");
        }

        if (repository.findByDeliveryId(dto.getDeliveryId()).isPresent()) {
            throw new InvalidRequestException(
                    "Proof of receipt already exists for this delivery");
        }

        ProofOfReceipt proof = ProofOfReceipt.builder()
                .deliveryId(dto.getDeliveryId())
                .departmentId(dto.getDepartmentId())
                .fileUri(dto.getFileUri())
                .status(dto.getStatus() != null ? dto.getStatus() : "RECEIVED")
                .build();

        ProofOfReceipt saved = repository.save(proof);
        return mapToResponse(saved);
    }

    // GET BY DELIVERY ID
    public ProofOfReceiptResponseDto getByDeliveryId(Integer deliveryId) {

        ProofOfReceipt proof = repository.findByDeliveryId(deliveryId)
                .orElseThrow(() ->
                        new InvalidRequestException(
                                "Proof not found for delivery: " + deliveryId));

        return mapToResponse(proof);
    }

    // GET BY DEPARTMENT ID
    public List<ProofOfReceiptResponseDto> getByDepartmentId(Integer departmentId) {

        return repository.findByDepartmentId(departmentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // GET BY PROOF ID
    public ProofOfReceiptResponseDto getById(Integer proofId) {

        ProofOfReceipt proof = repository.findById(proofId)
                .orElseThrow(() ->
                        new InvalidRequestException("Proof not found: " + proofId));

        return mapToResponse(proof);
    }

    // MAPPER
    private ProofOfReceiptResponseDto mapToResponse(ProofOfReceipt proof) {
        return new ProofOfReceiptResponseDto(
                proof.getProofId(),
                proof.getDeliveryId(),
                proof.getDepartmentId(),
                proof.getReceivedAt(),
                proof.getFileUri(),
                proof.getStatus()
        );
    }
}
