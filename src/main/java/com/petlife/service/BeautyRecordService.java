package com.petlife.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.petlife.model.BeautyItem;
import com.petlife.model.BeautyItemPrice;
import com.petlife.model.BeautyRecord;
import com.petlife.model.BeautyRecordForm;
import com.petlife.repository.BeautyItemPriceRepository;
import com.petlife.repository.BeautyItemRepository;
import com.petlife.repository.BeautyRecordRepository;

import jakarta.persistence.criteria.Predicate;

@Service
@Transactional(readOnly = true)
public class BeautyRecordService {

    private static final Set<String> ALLOWED_STATUS = Set.of("預約中", "已完成", "已取消");
    private static final Set<String> ALLOWED_SIZE = Set.of("小型", "中型", "大型");

    private final BeautyRecordRepository beautyRecordRepository;
    private final BeautyItemRepository beautyItemRepository;
    private final BeautyItemPriceRepository beautyItemPriceRepository;

    public BeautyRecordService(BeautyRecordRepository beautyRecordRepository,
                               BeautyItemRepository beautyItemRepository,
                               BeautyItemPriceRepository beautyItemPriceRepository) {
        this.beautyRecordRepository = beautyRecordRepository;
        this.beautyItemRepository = beautyItemRepository;
        this.beautyItemPriceRepository = beautyItemPriceRepository;
    }

    public List<BeautyRecord> search(Integer petId, Integer beautyId, String status) {
        Specification<BeautyRecord> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (petId != null) {
                predicates.add(criteriaBuilder.equal(root.get("petId"), petId));
            }
            if (beautyId != null) {
                predicates.add(criteriaBuilder.equal(root.get("beautyItem").get("beautyId"), beautyId));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status.trim()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return beautyRecordRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "recordId"));
    }

    public BeautyRecordForm getFormById(Integer recordId) {
        BeautyRecord record = getEntity(recordId);
        return toForm(record);
    }

    public List<BeautyItem> getBeautyItemsForSelect() {
        return beautyItemRepository.findAllByOrderByBeautyIdAsc();
    }

    public BeautyRecord getEntity(Integer recordId) {
        return beautyRecordRepository.findByRecordId(recordId)
                .orElseThrow(() -> new IllegalArgumentException("找不到對應的美容預約"));
    }

    @Transactional
    public void create(BeautyRecordForm form) {
        validate(form, false);

        BeautyRecord record = new BeautyRecord();
        applyForm(record, form);
        beautyRecordRepository.save(record);
    }

    @Transactional
    public void update(BeautyRecordForm form) {
        validate(form, true);

        BeautyRecord record = getEntity(form.getRecordId());
        applyForm(record, form);
        beautyRecordRepository.save(record);
    }

    @Transactional
    public void delete(Integer recordId) {
        BeautyRecord record = getEntity(recordId);
        beautyRecordRepository.delete(record);
    }

    private void applyForm(BeautyRecord record, BeautyRecordForm form) {
        BeautyItem beautyItem = beautyItemRepository.findByBeautyId(form.getBeautyId())
                .orElseThrow(() -> new IllegalArgumentException("找不到對應的美容項目"));

        BeautyItemPrice price = beautyItemPriceRepository
                .findByBeautyItemBeautyIdAndPetSize(form.getBeautyId(), form.getPetSize())
                .orElseThrow(() -> new IllegalArgumentException("找不到對應體型價格"));

        record.setPetId(form.getPetId());
        record.setBeautyDate(form.getBeautyDate());
        record.setBeautySlot(form.getBeautySlot().trim());
        record.setBeautyItem(beautyItem);
        record.setPetSize(form.getPetSize());
        record.setItemPrice(price.getItemPrice());
        record.setStatus(form.getStatus());
    }

    private void validate(BeautyRecordForm form, boolean needId) {
        if (needId && (form.getRecordId() == null || form.getRecordId() <= 0)) {
            throw new IllegalArgumentException("美容紀錄編號不正確");
        }
        if (form.getPetId() == null || form.getPetId() <= 0) {
            throw new IllegalArgumentException("寵物編號不正確");
        }
        if (form.getBeautyDate() == null) {
            throw new IllegalArgumentException("美容日期不可空白");
        }
        if (form.getBeautySlot() == null || form.getBeautySlot().trim().isEmpty()) {
            throw new IllegalArgumentException("美容時段不可空白");
        }
        if (form.getBeautyId() == null || form.getBeautyId() <= 0) {
            throw new IllegalArgumentException("美容項目不正確");
        }
        if (form.getPetSize() == null || !ALLOWED_SIZE.contains(form.getPetSize())) {
            throw new IllegalArgumentException("寵物體型不正確");
        }
        if (form.getStatus() == null || !ALLOWED_STATUS.contains(form.getStatus())) {
            throw new IllegalArgumentException("預約狀態不正確");
        }
    }

    private BeautyRecordForm toForm(BeautyRecord record) {
        BeautyRecordForm form = new BeautyRecordForm();
        form.setRecordId(record.getRecordId());
        form.setPetId(record.getPetId());
        form.setBeautyDate(record.getBeautyDate());
        form.setBeautySlot(record.getBeautySlot());
        form.setBeautyId(record.getBeautyId());
        form.setPetSize(record.getPetSize());
        form.setStatus(record.getStatus());
        return form;
    }
}
