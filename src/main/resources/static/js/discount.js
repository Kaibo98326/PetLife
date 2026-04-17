// ==================== 1. 資料層 (預留給未來接 RESTful API) ====================
let mockData = [
    { id: 101, status: 'active', name: '雙11寵物狂歡', type: 1, typeName: '百分比折扣', start: '2025-11-01', end: '2025-11-15', desc: '全館8折', val: 0.8, min: 0 },
    { id: 102, status: 'active', name: '春季新品滿額折', type: 2, typeName: '滿額折扣', start: '2022-03-01', end: '2022-03-31', desc: '滿千折百', val: 100, min: 1000 },
    { id: 103, status: 'inactive', name: '測試活動(已停用)', type: 1, typeName: '百分比折扣', start: '2026-01-01', end: '2026-12-31', desc: '測試', val: 0.9, min: 0 }
];

let formModal; 

// ==================== 2. 初始化與事件綁定 ====================
document.addEventListener("DOMContentLoaded", () => {
    formModal = new bootstrap.Modal(document.getElementById('formModal'));
    updateTime();
    
    // 未來替換為： fetch('/api/discounts').then(...).then(data => renderTable(data));
    renderTable(mockData);
    
    setInterval(updateTime, 1000);
});

function updateTime() {
    document.getElementById('lastUpdateTime').textContent = new Date().toLocaleTimeString('zh-TW', {hour: '2-digit', minute:'2-digit', second:'2-digit'});
}

// ==================== 3. 畫面渲染邏輯 ====================
function handleSearch() {
    const keyword = document.getElementById('searchInput').value.toLowerCase();
    const filteredData = mockData.filter(item => item.name.toLowerCase().includes(keyword));
    document.getElementById('searchCount').textContent = filteredData.length;
    renderTable(filteredData);
}

function renderTable(dataArray) {
    const tbody = document.getElementById('tableBody');
    tbody.innerHTML = '';
    document.getElementById('totalCount').textContent = mockData.length; 
    
    dataArray.forEach((row, index) => {
        const badge = getStatusBadge(row.status, row.start, row.end);
        const isOngoing = badge.includes('進行中');
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
                <button class="btn btn-sm btn-outline-info" data-discount-id="${row.id}" onclick="viewDetails(${row.id})">👁️</button>
                <button class="btn btn-sm btn-outline-primary" data-discount-id="${row.id}" onclick="editActivity(${row.id}, ${isOngoing})">✏️</button>
                <button class="btn btn-sm btn-outline-danger" data-discount-id="${row.id}" ${isOngoing ? 'disabled title="進行中的活動無法刪除"' : ''}>🗑️</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function getStatusBadge(status, startStr, endStr) {
    if (status === 'inactive') return '<span class="badge bg-danger rounded-pill px-3">🔴 已停用</span>';
    const now = new Date();
    const start = new Date(startStr);
    const end = new Date(endStr);
    end.setHours(23, 59, 59, 999); 
    if (now < start) return '<span class="badge bg-warning text-dark rounded-pill px-3">🟡 即將開始</span>';
    if (now >= start && now <= end) return '<span class="badge bg-success rounded-pill px-3">🟢 進行中</span>';
    return '<span class="badge bg-secondary rounded-pill px-3">⚪ 已結束</span>';
}

// ==================== 4. 表單與防呆邏輯 ====================
function renderDynamicFields() {
    const type = document.getElementById('discount_type_id').value;
    const area = document.getElementById('dynamicFieldsArea');
    const addon = document.getElementById('valueAddon');
    const label = document.getElementById('valueLabel');
    const hint = document.getElementById('valueHint');
    const input = document.getElementById('discount_value');

    if(!type) { area.style.display = 'none'; return; }
    area.style.display = 'block';

    if(type === '1') {
        label.innerHTML = '折扣比例 <span class="text-danger">*</span>';
        addon.innerText = '% Off (折)';
        hint.innerText = '👉 請輸入 1-99 整數 (例如填 85 代表打 85 折)';
        input.max = 99; input.placeholder = "85";
    } else if(type === '2') {
        label.innerHTML = '折抵金額 <span class="text-danger">*</span>';
        addon.innerText = '元';
        hint.innerText = '👉 請輸入折抵數值 (例如填 100 代表折 100 元)';
        input.removeAttribute('max'); input.placeholder = "100";
    }
}

function openAddModal() {
    document.getElementById('formModalTitle').innerHTML = '✨ 新增優惠活動';
    document.getElementById('activityForm').reset();
    document.getElementById('discount_type_id').disabled = false;
    document.getElementById('discount_value').disabled = false;
    document.getElementById('dynamicFieldsArea').style.display = 'none';
    document.getElementById('charCount').textContent = '0';
    new bootstrap.Tab(document.querySelector('#rules-tab')).show();
    formModal.show(); 
}

function updateEndDateMin() { document.getElementById('end_date').min = document.getElementById('start_date').value; }
function updateCharCount() { document.getElementById('charCount').textContent = document.getElementById('discount_description').value.length; }

function saveActivity() {
    const nameInput = document.getElementById('discount_name');
    const saveBtn = document.getElementById('saveBtn');

    if(!nameInput.value.trim()) {
        document.getElementById('nameError').style.display = 'block';
        new bootstrap.Tab(document.querySelector('#rules-tab')).show();
        setTimeout(() => nameInput.focus(), 200);
        return;
    }
    if(!document.getElementById('activityForm').reportValidity()) return;

    saveBtn.disabled = true;
    saveBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> 儲存中...';

    // 未來替換為： fetch('/api/discounts', { method: 'POST', body: JSON.stringify(...) })
    setTimeout(() => {
        alert(`✅ 資料已送出！`);
        saveBtn.disabled = false;
        saveBtn.innerHTML = '儲存並發布';
        formModal.hide(); 
    }, 1000);
}

function editActivity(id, isOngoing) {
    openAddModal(); 
    document.getElementById('formModalTitle').innerHTML = '✏️ 編輯優惠活動';
    
    // 未來替換為發送 API 取得單筆資料
    const data = mockData.find(d => d.id === id);
    document.getElementById('discount_name').value = data.name;
    document.getElementById('start_date').value = data.start;
    document.getElementById('end_date').value = data.end;
    document.getElementById('discount_type_id').value = data.type;
    document.getElementById('discount_description').value = data.desc;
    
    renderDynamicFields();
    document.getElementById('discount_value').value = data.type === 1 ? (data.val * 100) : data.val;
    document.getElementById('minimum_purchase_amount').value = data.min;

    if(isOngoing) {
        document.getElementById('discount_type_id').disabled = true;
        document.getElementById('discount_value').disabled = true;
        document.getElementById('valueHint').innerHTML += '<br><span class="text-danger fw-bold">⚠️ 進行中活動不可修改折扣設定</span>';
    }
    updateCharCount();
}

function viewDetails(id) {
    const data = mockData.find(d => d.id === id);
    const badge = getStatusBadge(data.status, data.start, data.end);
    document.getElementById('v_name').innerText = data.name;
    document.getElementById('v_status').innerHTML = badge;
    document.getElementById('v_period').innerText = `${data.start.replace(/-/g, '/')} ~ ${data.end.replace(/-/g, '/')}`;
    
    let ruleStr = data.typeName;
    if(data.type === 1) ruleStr += ` (打 ${data.val * 10} 折)`;
    if(data.type === 2) ruleStr += ` (折抵 $${data.val})`;
    document.getElementById('v_discount').innerText = ruleStr;
    document.getElementById('v_desc').innerText = data.desc;

    new bootstrap.Modal(document.getElementById('viewModal')).show();
}