package com.petlife.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.petlife.model.BeautyItem;
import com.petlife.model.BeautyItemForm;
import com.petlife.model.BeautyItemPrice;
import com.petlife.repository.BeautyItemRepository;

@Service
@Transactional(readOnly = true)
public class BeautyItemService {

    private final BeautyItemRepository beautyItemRepository;

    public BeautyItemService(BeautyItemRepository beautyItemRepository) {
        this.beautyItemRepository = beautyItemRepository;
    }

    public List<BeautyItem> findForList(String queryType, String queryValue) {
        String safeQueryType = queryType == null || queryType.isBlank() ? "all" : queryType;
        String safeQueryValue = queryValue == null ? "" : queryValue.trim();

        if ("id".equals(safeQueryType)) {
            if (safeQueryValue.isBlank()) {
                return beautyItemRepository.findAllByOrderByBeautyIdAsc();
            }
            try {
                Integer beautyId = Integer.valueOf(safeQueryValue);
                return beautyItemRepository.findByBeautyId(beautyId)
                        .map(List::of)
                        .orElse(List.of());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("美容項目編號格式不正確");
            }
        }

        if ("name".equals(safeQueryType)) {
            if (safeQueryValue.isBlank()) {
                return beautyItemRepository.findAllByOrderByBeautyIdAsc();
            }
            return beautyItemRepository.findByItemNameContainingOrderByBeautyIdAsc(safeQueryValue);
        }

        return beautyItemRepository.findAllByOrderByBeautyIdAsc();
    }

    public BeautyItemForm getFormById(Integer beautyId) {
        BeautyItem item = getEntity(beautyId);
        return toForm(item);
    }

    public List<BeautyItem> findAllForSelect() {
        return beautyItemRepository.findAllByOrderByBeautyIdAsc();
    }

    public BeautyItem getEntity(Integer beautyId) {
        return beautyItemRepository.findByBeautyId(beautyId)
                .orElseThrow(() -> new IllegalArgumentException("找不到對應的美容項目"));
    }

    @Transactional
    public void create(BeautyItemForm form) {
        validate(form, false);

        BeautyItem item = new BeautyItem();
        item.setItemName(form.getItemName().trim());
        item.setDescription(normalizeText(form.getDescription()));
        applyPrices(item, form);

        beautyItemRepository.save(item);
    }

    @Transactional
    public void update(BeautyItemForm form) {
        validate(form, true);

        BeautyItem item = getEntity(form.getBeautyId());
        item.setItemName(form.getItemName().trim());
        item.setDescription(normalizeText(form.getDescription()));
        applyPrices(item, form);

        beautyItemRepository.save(item);
    }

    @Transactional
    public void delete(Integer beautyId) {
        BeautyItem item = getEntity(beautyId);
        beautyItemRepository.delete(item);
    }

    private void applyPrices(BeautyItem item, BeautyItemForm form) {
        item.clearPrices();
        item.addPrice(buildPrice(item, "小型", form.getSmallPrice()));
        item.addPrice(buildPrice(item, "中型", form.getMediumPrice()));
        item.addPrice(buildPrice(item, "大型", form.getLargePrice()));
    }

    private BeautyItemPrice buildPrice(BeautyItem item, String petSize, BigDecimal priceValue) {
        BeautyItemPrice price = new BeautyItemPrice();
        price.setBeautyItem(item);
        price.setPetSize(petSize);
        price.setItemPrice(priceValue);
        return price;
    }

    private void validate(BeautyItemForm form, boolean needId) {
        if (needId && (form.getBeautyId() == null || form.getBeautyId() <= 0)) {
            throw new IllegalArgumentException("美容項目編號不正確");
        }
        if (form.getItemName() == null || form.getItemName().trim().isEmpty()) {
            throw new IllegalArgumentException("美容項目名稱不可空白");
        }
        validatePrice(form.getSmallPrice(), "小型價格");
        validatePrice(form.getMediumPrice(), "中型價格");
        validatePrice(form.getLargePrice(), "大型價格");
    }

    private void validatePrice(BigDecimal price, String fieldName) {
        if (price == null) {
            throw new IllegalArgumentException(fieldName + "不可空白");
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + "不可小於 0");
        }
    }

    private String normalizeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private BeautyItemForm toForm(BeautyItem item) {
        BeautyItemForm form = new BeautyItemForm();
        form.setBeautyId(item.getBeautyId());
        form.setItemName(item.getItemName());
        form.setDescription(item.getDescription());
        form.setSmallPrice(item.getSmallPrice());
        form.setMediumPrice(item.getMediumPrice());
        form.setLargePrice(item.getLargePrice());
        return form;
    }
}
