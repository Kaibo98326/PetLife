// ==================== 1. 資料層 ====================
var tableData = []; 

// 紀錄目前的篩選狀態
var filterState = {
    keyword: '',
    status: 'all',
    type: 'all',
    member: 'all'
};

// ==================== 2. 安全取得 Modal ====================
function getFormModal() {
    var el = document.getElementById('formModal');
    return bootstrap.Modal.getInstance(el) || new bootstrap.Modal(el);
}

function getViewModal() {
    var el = document.getElementById('viewModal');
    return bootstrap.Modal.getInstance(el) || new bootstrap.Modal(el);
}


fetchDiscountTypes();
fetchDiscountList();



// ==================== 3. 動態取得下拉選單與關鍵字機制 ====================
function fetchDiscountTypes() {
    const typesFromDB = [
        { discount_type_id: 1, discount_type_name: '百分比折扣 (打折)', discount_code: 'PERCENT' },
        { discount_type_id: 2, discount_type_name: '滿額折扣 (折現)', discount_code: 'AMOUNT' }
    ];

    const selectEl = document.getElementById('discount_type_id');
    if (!selectEl) return;
    
    selectEl.innerHTML = '<option value="" selected disabled>請先選擇折扣類型，以展開對應設定</option>';

    typesFromDB.forEach(type => {
        const option = document.createElement('option');
        option.value = type.discount_type_id;            
        option.textContent = type.discount_type_name;    
        option.setAttribute('data-keyword', type.discount_code); 
        selectEl.appendChild(option);
    });
}

function fetchDiscountList() {
    fetch('/api/discounts')
        .then(res => res.json())
        .then(data => {
            tableData = data.map(item => ({
                id: item.discountId,
                status: item.status,
                name: item.discountName,
                type: item.discountType ? item.discountType.discountTypeId : '',
                typeName: item.discountType ? item.discountType.discountTypeName : '未指定',
                start: item.startDate,
                end: item.endDate,
                desc: item.discountDescription,
                val: item.discountValue,
                min: item.minimumPurchaseAmount,
                isMember: item.isMember
            }));
            runAllFilters(); // 載入完後，自動套用當前篩選條件顯示
        })
        .catch(err => {
            console.error("取得資料失敗:", err);
            tableData = [];
            runAllFilters();
        });
}

// ==================== 4. 畫面渲染與篩選邏輯 ====================
function applyFilter(btn, value) {
    var group = btn.closest('.d-flex');
    group.querySelectorAll('.btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');

    var type = btn.closest('.filter-group').getAttribute('data-filter');
    filterState[type] = value;

    runAllFilters();
}

function clearFilters() {
    filterState = { keyword: document.getElementById('searchInput').value.toLowerCase(), status: 'all', type: 'all', member: 'all' };
    document.querySelectorAll('.filter-group').forEach(group => {
        group.querySelectorAll('.btn').forEach(b => b.classList.remove('active'));
        var allBtn = group.querySelector('.btn');
        if(allBtn) allBtn.classList.add('active');
    });
    var startInput = document.getElementById('filter_start_date');
    var endInput = document.getElementById('filter_end_date');
    if(startInput) startInput.value = '';
    if(endInput) endInput.value = '';
    
    document.getElementById('filterBadge').classList.add('d-none');
    runAllFilters();
}

function handleSearch() {
    filterState.keyword = document.getElementById('searchInput').value.toLowerCase();
    runAllFilters();
}

function runAllFilters() {
    var filtered = tableData.filter(item => {
        // 【微調 1】：加上 (item.name || '') 防呆，避免 null 造成 toLowerCase() 報錯
        var matchKeyword = (item.name || '').toLowerCase().includes(filterState.keyword);
        
        // B. 狀態過濾
        var badge = getStatusBadge(item.status, item.start, item.end);
        var matchStatus = (filterState.status === 'all');
       
        if (filterState.status === 'not_started') matchStatus = badge.includes('尚未開始');
        if (filterState.status === 'upcoming') matchStatus = badge.includes('即將開始');
        if (filterState.status === 'active') matchStatus = badge.includes('進行中');
        if (filterState.status === 'expired') matchStatus = badge.includes('已結束');
        if (filterState.status === 'inactive') matchStatus = badge.includes('已停用');

        var matchType = (filterState.type === 'all' || item.type.toString() === filterState.type);
        var matchMember = (filterState.member === 'all' || item.isMember.toString() === filterState.member);

        var matchDate = true;
        var filterStart = document.getElementById('filter_start_date') ? document.getElementById('filter_start_date').value : '';
        var filterEnd = document.getElementById('filter_end_date') ? document.getElementById('filter_end_date').value : '';
        
        if (filterStart || filterEnd) {
            var itemStart = new Date(item.start);
            var itemEnd = new Date(item.end);
            if (filterStart) matchDate = matchDate && (itemEnd >= new Date(filterStart));
            if (filterEnd) matchDate = matchDate && (itemStart <= new Date(filterEnd));
        }

        return matchKeyword && matchStatus && matchType && matchMember && matchDate;
    });

    // 	嚴謹的防呆寫法。先確認元素存在，再確認它的值不是空的。
	    var filterStartEl = document.getElementById('filter_start_date');
	    var filterEndEl = document.getElementById('filter_end_date');
	    
	    var isFiltered = filterState.status !== 'all' || 
	                     filterState.type !== 'all' || 
	                     filterState.member !== 'all' || 
	                     (filterStartEl && filterStartEl.value !== '') || 
	                     (filterEndEl && filterEndEl.value !== '');
                     
    // 判斷目前是否有輸入搜尋關鍵字
    var isSearchActive = filterState.keyword.trim() !== '';

    // 符合人類直覺的搜尋結果顯示邏輯
    var searchCountEl = document.getElementById('searchCount');
    if(searchCountEl) {
        if (isSearchActive || isFiltered) {
            // 有搜尋或篩選時，顯示過濾後的數量
            searchCountEl.textContent = filtered.length;
        } else {
            // 剛進畫面、毫無動作時，顯示 '-' 代表未搜尋
            searchCountEl.textContent = '-';
        }
    }
    
    // 更新「已套用」小紅點
    var badgeEl = document.getElementById('filterBadge');
    if (badgeEl) badgeEl.classList.toggle('d-none', !isFiltered);

    renderTable(filtered);
}


function renderTable(dataArray) {
    const tbody = document.getElementById('tableBody');
    if(!tbody) return;
    
    tbody.innerHTML = '';
    const countEl = document.getElementById('totalCount');
    if(countEl) countEl.textContent = tableData.length; 
    
    dataArray.forEach((row, index) => {
        const badge = getStatusBadge(row.status, row.start, row.end);
        const isOngoing = badge.includes('進行中');
        const isExpired = badge.includes('已結束');
        const isInactive = badge.includes('已停用');
        
        // 定義「鎖定狀態」(已結束或已停用)
        const isLocked = isExpired || isInactive; 
        const period = `${row.start.replace(/-/g, '/')} - ${row.end.replace(/-/g, '/')}`;
        const displayIndex = index + 1;

		const tr = document.createElement('tr');
		        tr.setAttribute('data-discount-id', row.id); 
		        tr.innerHTML = `
		            <td>${displayIndex}</td>
		            <td>${badge}</td>
		            <td class="fw-bold">${row.name}</td>
		            <td>${row.typeName}</td>
		            <td>${period}</td>
		            <td>
		                <button class="btn btn-sm text-primary border-0 bg-transparent px-2 py-1 fw-bold text-start" 
		                        style="transition: 0.2s; width: 105px;" 
		                        onmouseover="this.classList.add('bg-light')" 
		                        onmouseout="this.classList.remove('bg-light')" 
		                        title="${isLocked ? '查看歷史資料' : '查看和修改活動'}" 
		                        onclick="viewAndEditActivity(${row.id}, ${isOngoing}, ${isLocked})">
		                    <i class="fa-solid ${isLocked ? 'fa-eye' : 'fa-folder-closed'}"></i> ${isLocked ? '查看' : '查看/修改'}
		                </button>
		                
		                <button class="btn btn-sm text-danger border-0 bg-transparent px-2 py-1 fw-bold text-start" 
		                        style="transition: 0.2s;" 
		                        onmouseover="this.classList.add('bg-light')" 
		                        onmouseout="this.classList.remove('bg-light')" 
		                        title="刪除活動" data-discount-id="${row.id}" 
		                        onclick="deleteActivity(${row.id}, ${isOngoing})">
		                    <i class="fa-regular fa-trash-can"></i> 刪除
		                </button>
		            </td>
		        `;
        tbody.appendChild(tr);
    });
}
function getStatusBadge(status, startStr, endStr) {
    // 【修正 2】：定義標準化的字體與排版樣式 (稍微放大、好看的黑體字、一致的飽滿內距)
    const badgeStyle = "font-size: 0.9rem; font-family: 'Noto Sans TC', 'Microsoft JhengHei', sans-serif; letter-spacing: 0.5px; padding: 0.45em 0.85em;";

    // 【修正 1】：在所有標籤加入 title 屬性，達成業界標準的 Tooltip 工具提示效果
    if (status === 'inactive') {
        return `<span class="badge bg-danger rounded-pill" style="${badgeStyle}" title="管理員手動關閉。">🔴 已停用</span>`;
    }
    
    const now = new Date();
    const start = new Date(startStr);
    const end = new Date(endStr);
    end.setHours(23, 59, 59, 999); 
    
    if (now < start) {
        const timeDiff = start.getTime() - now.getTime();
        const hoursDiff = timeDiff / (1000 * 60 * 60);
        if (hoursDiff <= 24) {
            return `<span class="badge bg-warning text-dark rounded-pill" style="${badgeStyle}" title="倒數 24 小時內，準備開跑。">🟡 即將開始</span>`;
        } else {
            return `<span class="badge bg-info text-dark rounded-pill" style="${badgeStyle}" title="離活動開始還有 24 小時以上。">🔵 尚未開始</span>`;
        }
    }
    if (now >= start && now <= end) {
        return `<span class="badge bg-success rounded-pill" style="${badgeStyle}" title="消費者現在可以使用此折扣結帳。">🟢 進行中</span>`;
    }
    return `<span class="badge bg-secondary rounded-pill" style="${badgeStyle}" title="活動時間已過，僅供查閱。">⚪ 已結束</span>`;
}

function deleteActivity(id, isOngoing) {
    // 如果是進行中的活動，攔截並顯示漂亮警告 (這裡使用 alert 格式模擬，建議未來可換成 SweetAlert2)
    if (isOngoing) {
        alert(`⚠️ 無法刪除！\n\n為了確保進行中的訂單金額計算正確，系統禁止刪除「進行中」活動。\n\n👉 如需停止該活動，請點擊「修改」並將狀態改為「已停用」。`);
        return;
    }

    if (!confirm('確定要刪除這筆活動嗎？')) return;

    fetch(`/api/discounts/${id}`, { method: 'DELETE' })
        .then(response => {
            if (!response.ok) throw new Error('刪除失敗');
            alert('✅ 活動已刪除！');
            fetchDiscountList(); 
        })
        .catch(error => alert('❌ ' + error.message));
}

// ==================== 5. 表單與動態對應邏輯 ====================
var currentEditId = null; 
var currentIsOngoing = false; 
var currentIsLocked = false; // 【新增】：紀錄是否為已結束或已停用的唯讀狀態

function setFormDisabled(disabled) {
    var fields = ['discount_name', 'status', 'start_date', 'end_date', 'discount_type_id', 'discount_description', 'is_member', 'minimum_purchase_amount', 'discount_value'];
    fields.forEach(f => {
        var el = document.getElementById(f);
        if(el) {
            el.disabled = disabled;
            el.style.display = ''; // 恢復顯示輸入框
            
            // 【修正】：如果是折扣值，也要把整個外層 input-group 恢復顯示
            if (f === 'discount_value') {
                var group = el.closest('.input-group');
                if(group) group.style.display = '';
            }
        }
        var textEl = document.getElementById(f + '_text');
        if(textEl) textEl.remove();
    });
    
    var alertDiv = document.getElementById('ongoingAlert');
    if(alertDiv) alertDiv.remove();
    
    var hint = document.getElementById('valueHint');
    if(hint) hint.style.display = '';
}

//根據鎖定狀態隱藏「修改」按鈕
function setupFooterButtons(mode, isLocked) {
    var footer = document.querySelector('#formModal .modal-footer');
    if(!footer) return;

    if (mode === 'view') {
        footer.innerHTML = `
            ${!isLocked ? '<button type="button" class="btn btn-primary px-4" onclick="enableEditMode()">修改</button>' : ''}
            <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">關閉</button>
        `;
    } else if (mode === 'edit') {
        footer.innerHTML = `
            <button type="button" class="btn btn-primary px-4" id="saveBtn" onclick="saveActivity()">確定</button>
            <button type="button" class="btn btn-outline-secondary" onclick="cancelEditMode()">取消</button>
        `;
    } else if (mode === 'add') {
        footer.innerHTML = `
            <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">取消</button>
            <button type="button" class="btn btn-primary px-4" id="saveBtn" onclick="saveActivity()">儲存並發布</button>
        `;
    }
}

function openAddModal() {
    currentEditId = null; 
    currentIsOngoing = false;
    var modal = getFormModal(); 
    var formTitle = document.getElementById('formModalTitle');
    if (formTitle) formTitle.innerHTML = '✨ 新增優惠活動';
    
    var form = document.getElementById('activityForm');
    if (form) form.reset();
    
    setFormDisabled(false); 
    document.getElementById('dynamicFieldsArea').style.display = 'none';
    
    var charCountEl = document.getElementById('charCount');
    if(charCountEl) charCountEl.textContent = '0';
    
    var today = new Date().toISOString().split('T')[0];
    var startDateInput = document.getElementById('start_date');
    if(startDateInput) startDateInput.min = today;
    
    setupFooterButtons('add'); 
    
    var warning = document.getElementById('ongoingWarning');
    if(warning) warning.remove();
    
    var triggerEl = document.querySelector('#rules-tab');
    if(triggerEl) triggerEl.click(); 
    
    modal.show(); 
}

function viewAndEditActivity(id, isOngoing, isLocked) {
    var data = tableData.find(d => d.id === id);
    if (!data) return;

    currentEditId = id; 
    currentIsOngoing = isOngoing;
    currentIsLocked = isLocked; // 紀錄鎖定狀態

    var modal = getFormModal();
    var formTitle = document.getElementById('formModalTitle');
    
    // 根據狀態切換標題
    if (formTitle) {
        formTitle.innerHTML = isLocked ? '📜 查看活動歷史 (唯讀)' : '📁 查看與修改活動';
    }
    
    // ... (中間的欄位賦值與 renderDynamicFields 不動) ...

    setFormDisabled(true); // 初始全部鎖死
    setupFooterButtons('view', isLocked); // 傳入鎖定狀態
    updateCharCount();

    var triggerEl = document.querySelector('#rules-tab');
    if(triggerEl) triggerEl.click();

    modal.show();
}

function enableEditMode() {
    var formTitle = document.getElementById('formModalTitle');
    if (formTitle) formTitle.innerHTML = '✏️ 修改優惠活動';

    setFormDisabled(false); 

    var startDateInput = document.getElementById('start_date');
    var endDateInput = document.getElementById('end_date');
    var today = new Date().toISOString().split('T')[0];

    if (currentIsOngoing) {
        var form = document.getElementById('activityForm');
        var alertDiv = document.createElement('div');
        alertDiv.id = 'ongoingAlert';
        alertDiv.className = 'alert alert-warning border-0 shadow-sm mb-4 d-flex align-items-center';
        alertDiv.innerHTML = `
            <i class="fa-solid fa-circle-info fs-4 me-3"></i>
            <div>
                <strong class="d-block">這是一個進行中的活動</strong>
                為了保障消費者權益，涉及金額計算與開始時間的欄位已自動鎖定。若需停止活動，請修改「結束時間」或將狀態改為「已停用」。
            </div>
        `;
        form.prepend(alertDiv);

        if(endDateInput) endDateInput.min = today;

        var lockedFields = [
            { id: 'start_date', type: 'input' },
            { id: 'discount_type_id', type: 'select' },
            { id: 'discount_value', type: 'input', suffix: document.getElementById('valueAddon')?.innerText || '' },
            { id: 'minimum_purchase_amount', type: 'input', prefix: 'NT$ ' },
            { id: 'is_member', type: 'select' }
        ];

        lockedFields.forEach(field => {
            var el = document.getElementById(field.id);
            if (!el) return;
            
            el.disabled = true;
            
            // 【修正】：針對 discount_value 隱藏整個 input-group，避免後面的 % 掉到下一行
            if (field.id === 'discount_value') {
                var group = el.closest('.input-group');
                if (group) group.style.display = 'none';
            } else {
                el.style.display = 'none'; 
            }

            var textSpan = document.createElement('div');
            textSpan.className = 'form-control-plaintext fw-bold text-dark ps-2 bg-light rounded border';
            textSpan.id = field.id + '_text';
            
            var displayVal = "";
            if(field.type === 'select') {
                displayVal = el.options[el.selectedIndex].text;
            } else {
                displayVal = (field.prefix || "") + el.value + (field.suffix || "");
            }
            textSpan.innerText = displayVal;
            
            // 【修正】：正確將純文字插入到 input-group 的後方
            if (field.id === 'discount_value') {
                var group = el.closest('.input-group');
                if (group) group.parentNode.insertBefore(textSpan, group.nextSibling);
            } else {
                el.parentNode.insertBefore(textSpan, el.nextSibling);
            }
        });
        
        var hint = document.getElementById('valueHint');
        if(hint) hint.style.display = 'none';

    } else {
        if (startDateInput) {
            var data = tableData.find(d => d.id === currentEditId);
            var badge = getStatusBadge(data.status, data.start, data.end);
            if (badge.includes('尚未開始') || badge.includes('即將開始')) {
                startDateInput.min = today;
            } else {
                startDateInput.removeAttribute('min');
            }
        }
    }

    setupFooterButtons('edit'); 
}

function cancelEditMode() {
    viewAndEditActivity(currentEditId, currentIsOngoing);
}

function renderDynamicFields() {
    var selectEl = document.getElementById('discount_type_id');
    var selectedOption = selectEl.options[selectEl.selectedIndex];
    var area = document.getElementById('dynamicFieldsArea');
    var addon = document.getElementById('valueAddon');
    var label = document.getElementById('valueLabel');
    var input = document.getElementById('discount_value');

    if(!selectedOption || !selectedOption.value) { 
        area.style.display = 'none'; 
        return; 
    }
    
    area.style.display = 'block';
    var keyword = selectedOption.getAttribute('data-keyword');

    if(keyword === 'PERCENT') {
        label.innerHTML = '折扣比例 <span class="text-danger">*</span>';
        addon.innerText = '%'; 
        input.max = 99; 
        input.placeholder = "輸入 85 代表打 85 折";
    } else if(keyword === 'AMOUNT') {
        label.innerHTML = '折抵金額 <span class="text-danger">*</span>';
        addon.innerText = '元'; 
        input.removeAttribute('max'); 
        input.placeholder = "輸入 100 代表折 100 元";
    }
}

function updateEndDateMin() { 
    var start = document.getElementById('start_date').value;
    var end = document.getElementById('end_date');
    if(end) end.min = start; 
}

function updateCharCount() { 
    var desc = document.getElementById('discount_description');
    var count = document.getElementById('charCount');
    if(desc && count) count.textContent = desc.value.length; 
}

function saveActivity() {
    var nameInput = document.getElementById('discount_name');
    var saveBtn = document.getElementById('saveBtn');

    if(!nameInput.value.trim()) {
        document.getElementById('nameError').style.display = 'block';
        var triggerEl = document.querySelector('#rules-tab');
        if(triggerEl) triggerEl.click();
        setTimeout(() => nameInput.focus(), 200);
        return;
    }
    
	var form = document.getElementById('activityForm');
	    // 注意：disabled 的欄位不會被 reportValidity 檢查
	    if(form && !form.reportValidity()) return;

		saveBtn.disabled = true;
		    saveBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> 處理中...';

    var selectEl = document.getElementById('discount_type_id');
    var selectedOption = selectEl.options[selectEl.selectedIndex];
    
    var payload = {
        discountName: nameInput.value.trim(),
        status: document.getElementById('status').value,
        startDate: document.getElementById('start_date').value,
        endDate: document.getElementById('end_date').value,
        discountDescription: document.getElementById('discount_description').value,
        discountValue: parseFloat(document.getElementById('discount_value').value) || 0,
        minimumPurchaseAmount: parseFloat(document.getElementById('minimum_purchase_amount').value) || 0,
        isMember: document.getElementById('is_member').value === 'true',
        discountType: {
            discountTypeId: parseInt(selectEl.value)
        }
    };

    var keyword = selectedOption ? selectedOption.getAttribute('data-keyword') : '';
    if (keyword === 'PERCENT') {
        payload.discountValue = payload.discountValue / 100;
    } else if (keyword === 'AMOUNT') {
        payload.discountValue = Math.abs(payload.discountValue);
    }

    var apiUrl = currentEditId ? `/api/discounts/${currentEditId}` : '/api/discounts';
    var httpMethod = currentEditId ? 'PUT' : 'POST';

    fetch(apiUrl, {
        method: httpMethod,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
    .then(response => {
        if (!response.ok) throw new Error('伺服器錯誤碼: ' + response.status);
        return response.json();
    })
    .then(data => {
        alert(`✅ 資料已成功${currentEditId ? '修改' : '新增'}！`);
        getFormModal().hide(); 
        fetchDiscountList(); 
    })
    .catch(error => {
        console.error('Error:', error);
        alert('❌ 處理失敗：' + error.message);
        saveBtn.disabled = false;
        saveBtn.innerHTML = currentEditId ? '確定' : '儲存並發布';
    });
}