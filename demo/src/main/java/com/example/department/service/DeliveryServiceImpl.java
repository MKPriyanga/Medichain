package com.example.department.service;

import com.example.department.entity.DeliveryRecord;
import com.example.department.entity.ProofOfReceipt;
import com.example.department.exception.InvalidRequestException;
import com.example.department.exception.UnauthorizedRoleException;
import com.example.department.repository.DeliveryRecordRepository;
import com.example.department.repository.ProofOfReceiptRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    @Autowired
    private DeliveryRecordRepository deliveryRepo;

    @Autowired
    private ProofOfReceiptRepository proofRepo;

    /* ✅ CREATE DELIVERY */
    @Override
    public void createDelivery(String role, Map<String, Object> body) {

        if (!role.equalsIgnoreCase("ADMIN")
                && !role.equalsIgnoreCase("STORE")) {
            throw new UnauthorizedRoleException(
                    "Only Admin or Store can create delivery");
        }

        Integer requestId =
                Integer.parseInt(body.get("requestId").toString());
        Integer quantity =
                Integer.parseInt(body.get("quantity").toString());
        String deliveredBy = body.get("deliveredBy").toString();

        if (quantity <= 0) {
            throw new InvalidRequestException(
                    "Quantity must be greater than zero");
        }

        if (deliveryRepo.existsByRequestId(requestId)) {
            throw new InvalidRequestException(
                    "Delivery already exists for this request");
        }

        DeliveryRecord delivery = new DeliveryRecord();
        delivery.setRequestId(requestId);
        delivery.setDeliveredBy(deliveredBy);
        delivery.setQuantity(quantity);
        delivery.setStatus("DELIVERED");

        deliveryRepo.save(delivery);
    }

    /* ✅ LIST DELIVERIES */
    @Override
    public List<DeliveryRecord> listDeliveries(String role, Integer requestId) {

        if (!role.equalsIgnoreCase("ADMIN")
                && !role.equalsIgnoreCase("STORE")
                && !role.equalsIgnoreCase("DEPARTMENT_HEAD")) {
            throw new UnauthorizedRoleException("Access denied");
        }

        return requestId == null
                ? deliveryRepo.findAll()
                : deliveryRepo.findByRequestId(requestId);
    }

    /* ✅ UPLOAD PROOF */
    @Override
    public void uploadProof(String role, Map<String, Object> body) {

        if (!role.equalsIgnoreCase("DOCTOR")
                && !role.equalsIgnoreCase("NURSE")
                && !role.equalsIgnoreCase("DEPARTMENT_HEAD")) {
            throw new UnauthorizedRoleException(
                    "Access denied");
        }

        Integer deliveryId =
                Integer.parseInt(body.get("deliveryId").toString());
        Integer departmentId =
                Integer.parseInt(body.get("departmentId").toString());
        String fileUri =
                body.get("fileUri").toString();

        DeliveryRecord delivery =
                deliveryRepo.findById(deliveryId)
                        .orElseThrow(() ->
                                new InvalidRequestException(
                                        "Delivery not found"));

        if (!"DELIVERED".equalsIgnoreCase(delivery.getStatus())) {
            throw new InvalidRequestException(
                    "Proof allowed only for DELIVERED delivery");
        }

        if (proofRepo.existsByDeliveryId(deliveryId)) {
            throw new InvalidRequestException(
                    "Proof already uploaded");
        }

        ProofOfReceipt proof = new ProofOfReceipt();
        proof.setDeliveryId(deliveryId);
        proof.setDepartmentId(departmentId);
        proof.setFileUri(fileUri);
        proof.setStatus("RECEIVED");

        proofRepo.save(proof);
    }

    /* ✅ CLOSE REQUEST */
    @Override
    public void closeRequest(String role, Integer deliveryId) {

        if (!role.equalsIgnoreCase("ADMIN")
                && !role.equalsIgnoreCase("DEPARTMENT_HEAD")) {
            throw new UnauthorizedRoleException("Access denied");
        }

        DeliveryRecord delivery =
                deliveryRepo.findById(deliveryId)
                        .orElseThrow(() ->
                                new InvalidRequestException(
                                        "Delivery not found"));

        if (!"DELIVERED".equalsIgnoreCase(delivery.getStatus())) {
            throw new InvalidRequestException(
                    "Only DELIVERED requests can be closed");
        }

        if (proofRepo.countByDeliveryId(deliveryId) == 0) {
            throw new InvalidRequestException(
                    "Proof of receipt required to close request");
        }

        delivery.setStatus("CLOSED");
        deliveryRepo.save(delivery);
    }
}