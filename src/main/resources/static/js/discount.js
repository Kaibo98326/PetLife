// ==================== 1. 資料層 (Data Layer) ====================

// 宣告一個全域陣列，用來存放從後端 API 抓取回來的原始折扣資料
//暫存資料的地方，執行 fetchDiscountList() 時，程式會去後端 API 抓資料存進去
//搜尋或篩選活動時，程式不會再去後端抓一次，使用tableData舊資料比對
//顯示「總活動數」時，也是直接看這個倉庫裡總共有幾件商品 (tableData.length)
//新增成功時呼叫 fetchDiscountList()->
// 更新全域變數 tableData->runAllFilters() 進行重新計算->renderTable() 把最新的資料重新畫在表格上
//不刷新的 Ajax 網頁都需要暫存吃資料的地方
var tableData = []; 

// 紀錄目前網頁上的篩選狀態，包含關鍵字、活動狀態、類型與會員資格
//同時「輸入搜尋字」又「點擊進行中按鈕」
var filterState = {
//	紀錄使用者在搜尋框輸入的「活動名稱」關鍵字
    keyword: '',
//	紀錄使用者選取的「活動狀態」
    status: 'all',
//	紀錄使用者選取的「活動類型
    type: 'all',
//	紀錄是否限會員
    member: 'all'
};

// ==================== 2. 安全取得 Modal (Bootstrap 互動視窗) ====================

// 取得新增/修改表單的 Modal 實體；若已存在則直接取得，不存在則新建一個
function getFormModal() {
//	抓取html網頁新增formModal卡片
    var el = document.getElementById('formModal');
//	Bootstrap中，如果你對同一個HTML元素執行兩次 new bootstrap.Modal()，記憶體裡會產生兩個控制器去搶同一個框框
//	先用 getInstance 檢查，有舊的就用舊的，沒舊的才建新的
    return bootstrap.Modal.getInstance(el) || new bootstrap.Modal(el);
}

// 取得純檢視模式的 Modal 實體 (邏輯同上)
function getViewModal() {
    var el = document.getElementById('viewModal');
    return bootstrap.Modal.getInstance(el) || new bootstrap.Modal(el);
}


// ==================== 啟動 SPA 頁面監聽雷達 (SPA Listener) ====================

// 標記位：紀錄使用者目前是否正停留在「優惠活動管理」的畫面上
//現在是不是已經在處理優惠頁面了?
var isDiscountPageActive = false; 

// 設定定時器，每 0.3 秒掃描一次 DOM 結構
setInterval(function() {
    // 檢查頁面上是否存在 discount_tableBody 這個表格元素
//	利用getElementById去DOM Tree（網頁結構樹裡面搜尋tbody就會有值,使用者在別的頁面（例如員工中心），tbody 就會是 null

    var tbody = document.getElementById('discount_tableBody');
    
    // 如果找到了表格，但雷達顯示「尚未啟動」，代表使用者剛切換過來
    if (tbody && !isDiscountPageActive) {
		isDiscountPageActive = true; // 啟動雷達
		    fetchDiscountTypes();        // 立即向後端請求折扣類型
		    fetchDiscountList();         // 立即向後端請求完整清單
    } 
    // 如果表格消失了，但雷達顯示「啟動中」，代表使用者切換到其他功能選單了
    else if (!tbody && isDiscountPageActive) {
        isDiscountPageActive = false; // 關閉雷達，重置狀態
    }
//	設定一個每 0.3 秒
}, 300); 


// ==================== 3. 資料獲取機制 (Fetch Mechanism) ====================

// 從後端或靜態定義取得折扣類型（如：打折或折現）並填入下拉選單
function fetchDiscountTypes() {
    // 模擬從資料庫取得的折扣種類資料
    const typesFromDB = [
        { discount_type_id: 1, discount_type_name: '百分比折扣 (打折)', discount_code: 'PERCENT' },
        { discount_type_id: 2, discount_type_name: '滿額折扣 (折現)', discount_code: 'AMOUNT' }
    ];

    // 尋找表單中的下拉選單元素
    const selectEl = document.getElementById('discount_type_id');
    if (!selectEl) return;
    
    // 初始化選單內容
    selectEl.innerHTML = '<option value="" selected disabled>請先選擇折扣類型，以展開對應設定</option>';

    // 迴圈跑每一種類型，並將其轉為 HTML 的 option 標籤
    typesFromDB.forEach(type => {
        const option = document.createElement('option');
        option.value = type.discount_type_id;            
        option.textContent = type.discount_type_name;    
        // 儲存 PERCENT 或 AMOUNT 作為關鍵字，供後續切換單位 (% 或 元) 使用
        option.setAttribute('data-keyword', type.discount_code); 
        selectEl.appendChild(option);
    });
}

// 發送 Ajax 請求向後端獲取所有折扣活動的 JSON 清單
function fetchDiscountList() {
    fetch('/api/discounts')
        .then(res => res.json())
        .then(data => {
            // 將後端回傳的原始資料映射(Map)成前端更易讀的物件格式
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
            // 資料載入完畢後，執行過濾邏輯並渲染表格
            runAllFilters(); 
        })
        .catch(err => {
            console.error("取得資料失敗:", err);
            tableData = [];
            runAllFilters();
        });
}

// ==================== 4. 畫面渲染與過濾邏輯 (Filter & Render) ====================

// 當使用者點擊篩選按鈕（如：進行中、已結束）時觸發
function applyFilter(btn, value) {
    // 取得該按鈕所屬的群組並移除其他按鈕的「選中」樣式
    var group = btn.closest('.d-flex');
    group.querySelectorAll('.btn').forEach(b => b.classList.remove('active'));
    // 為被點擊的按鈕加上選中樣式
    btn.classList.add('active');

    // 根據父層容器標記，判斷使用者是在篩選 status、type 還是 member
    var type = btn.closest('.filter-group').getAttribute('data-filter');
    filterState[type] = value;

    // 重新運算過濾並刷新畫面
    runAllFilters();
}

// 清除所有篩選條件並重置 UI 狀態
function clearFilters() {
    // 重置篩選狀態，但保留目前的搜尋關鍵字
    filterState = { keyword: document.getElementById('searchInput').value.toLowerCase(), status: 'all', type: 'all', member: 'all' };
    
    // 將所有篩選群組的按鈕狀態重置回「全部」
    document.querySelectorAll('.filter-group').forEach(group => {
        group.querySelectorAll('.btn').forEach(b => b.classList.remove('active'));
        var allBtn = group.querySelector('.btn');
        if(allBtn) allBtn.classList.add('active');
    });

    // 清空日期篩選框的值
    var startInput = document.getElementById('filter_start_date');
    var endInput = document.getElementById('filter_end_date');
    if(startInput) startInput.value = '';
    if(endInput) endInput.value = '';
    
    // 隱藏「已套用」的小紅點標籤
    document.getElementById('filterBadge').classList.add('d-none');
    runAllFilters();
}

// 處理文字搜尋輸入事件
function handleSearch() {
    // 將輸入的關鍵字存入全域狀態並轉為小寫
    filterState.keyword = document.getElementById('searchInput').value.toLowerCase();
    runAllFilters();
}

// 核心過濾函式：綜合判斷關鍵字、狀態、類型與日期區間
function runAllFilters() {
    var filtered = tableData.filter(item => {
        // A. 搜尋名稱：比對活動名稱是否包含關鍵字，(item.name || '') 用來防止資料為 null 報錯
        var matchKeyword = (item.name || '').toLowerCase().includes(filterState.keyword);
        
        // B. 狀態過濾：先取得當前時間計算出的狀態標籤
        var badge = getStatusBadge(item.status, item.start, item.end);
        var matchStatus = (filterState.status === 'all');
       
        // 依據篩選按鈕的值比對標籤文字
        if (filterState.status === 'not_started') matchStatus = badge.includes('尚未開始');
        if (filterState.status === 'upcoming') matchStatus = badge.includes('即將開始');
        if (filterState.status === 'active') matchStatus = badge.includes('進行中');
        if (filterState.status === 'expired') matchStatus = badge.includes('已結束');
        if (filterState.status === 'inactive') matchStatus = badge.includes('已停用');

        // C. 類型與會員比對
        var matchType = (filterState.type === 'all' || item.type.toString() === filterState.type);
        var matchMember = (filterState.member === 'all' || item.isMember.toString() === filterState.member);

        // D. 日期範圍比對
        var matchDate = true;
        var filterStart = document.getElementById('filter_start_date') ? document.getElementById('filter_start_date').value : '';
        var filterEnd = document.getElementById('filter_end_date') ? document.getElementById('filter_end_date').value : '';
        
        if (filterStart || filterEnd) {
            var itemStart = new Date(item.start);
            var itemEnd = new Date(item.end);
            // 判斷活動區間是否與篩選區間重疊
            if (filterStart) matchDate = matchDate && (itemEnd >= new Date(filterStart));
            if (filterEnd) matchDate = matchDate && (itemStart <= new Date(filterEnd));
        }

        // 回傳所有條件皆符合的資料
        return matchKeyword && matchStatus && matchType && matchMember && matchDate;
    });

    // 檢查是否有任何進階篩選條件被啟動
    var filterStartEl = document.getElementById('filter_start_date');
    var filterEndEl = document.getElementById('filter_end_date');
    var isFiltered = filterState.status !== 'all' || 
                     filterState.type !== 'all' || 
                     filterState.member !== 'all' || 
                     (filterStartEl && filterStartEl.value !== '') || 
                     (filterEndEl && filterEndEl.value !== '');
                     
    // 檢查搜尋框是否有輸入
    var isSearchActive = filterState.keyword.trim() !== '';

    // 更新搜尋結果計數顯示：若有操作才顯示數量，否則顯示 '-'
    var searchCountEl = document.getElementById('searchCount');
    if(searchCountEl) {
        searchCountEl.textContent = (isSearchActive || isFiltered) ? filtered.length : '-';
    }
    
    // 根據是否套用篩選來切換小紅點的顯示狀態
    var badgeEl = document.getElementById('filterBadge');
    if (badgeEl) badgeEl.classList.toggle('d-none', !isFiltered);

    // 將篩選後的資料丟入表格繪製函式
    renderTable(filtered);
}

// 將資料陣列動態生成為 HTML 表格列 (tr)
function renderTable(dataArray) {
    const tbody = document.getElementById('discount_tableBody');
    if(!tbody) return; 

    tbody.innerHTML = ''; // 清空舊內容
    const countEl = document.getElementById('totalCount');
    if(countEl) countEl.textContent = tableData.length; // 更新總數顯示
    
    dataArray.forEach((row, index) => {
        // 判斷當前資料的狀態與是否被鎖定 (已結束/已停用)
        const badge = getStatusBadge(row.status, row.start, row.end);
        const isOngoing = badge.includes('進行中');
        const isExpired = badge.includes('已結束');
        const isInactive = badge.includes('已停用');
        const isLocked = isExpired || isInactive; 
        
        // 格式化日期顯示並設定流水號
        const period = `${row.start.replace(/-/g, '/')} - ${row.end.replace(/-/g, '/')}`;
        const displayIndex = index + 1;

        const tr = document.createElement('tr');
        tr.setAttribute('data-discount-id', row.id); 
        // 組合 HTML 內容，並根據鎖定狀態動態切換按鈕文字與圖示
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

// 根據時間與資料庫狀態回傳帶有樣式的 HTML 狀態標籤
function getStatusBadge(status, startStr, endStr) {
    const badgeStyle = "font-size: 0.9rem; font-family: 'Noto Sans TC', 'Microsoft JhengHei', sans-serif; letter-spacing: 0.5px; padding: 0.45em 0.85em;";

    // 如果後端標記為停用，優先權最高
    if (status === 'inactive') {
        return `<span class="badge bg-danger rounded-pill" style="${badgeStyle}" title="管理員手動關閉。">🔴 已停用</span>`;
    }
    
    const now = new Date();
    
    // 【精準修正】：將字串的 '-' 換成 '/'，強迫瀏覽器使用「台灣本地時區」解析
    const start = new Date(startStr.replace(/-/g, '/'));
    start.setHours(0, 0, 0, 0); // 鎖定在當天凌晨 00:00:00 開跑
    
    // 【精準修正】：同理，確保結束時間也沒有時差
    const end = new Date(endStr.replace(/-/g, '/'));
    end.setHours(23, 59, 59, 999); // 鎖定在當天最後一秒結束
    
    // 時間邏輯判斷：尚未開始/即將開始 (24小時內)/進行中/已結束
    if (now < start) {
        const timeDiff = start.getTime() - now.getTime();
        const hoursDiff = timeDiff / (1000 * 60 * 60);
        return hoursDiff <= 24 
            ? `<span class="badge bg-warning text-dark rounded-pill" style="${badgeStyle}" title="倒數 24 小時內，準備開跑。">🟡 即將開始</span>`
            : `<span class="badge bg-info text-dark rounded-pill" style="${badgeStyle}" title="離活動開始還有 24 小時以上。">🔵 尚未開始</span>`;
    }
    if (now >= start && now <= end) {
        return `<span class="badge bg-success rounded-pill" style="${badgeStyle}" title="消費者現在可以使用此折扣結帳。">🟢 進行中</span>`;
    }
    return `<span class="badge bg-secondary rounded-pill" style="${badgeStyle}" title="活動時間已過，僅供查閱。">⚪ 已結束</span>`;
}

// 執行活動刪除動作
function deleteActivity(id, isOngoing) {
    // 安全機制：禁止刪除進行中的活動，以維護訂單金額一致性
    if (isOngoing) {
        alert(` 無法刪除！\n\n系統禁止刪除「進行中」活動。\n\n👉 如需停止，請將狀態改為「已停用」。`);
        return;
    }

    if (!confirm('確定要刪除這筆活動嗎？')) return;

    // 向後端發送 DELETE 請求
    fetch(`/api/discounts/${id}`, { method: 'DELETE' })
        .then(response => {
            if (!response.ok) throw new Error('刪除失敗');
            alert(' 活動已刪除！');
            fetchDiscountList(); // 成功後刷新列表
        })
        .catch(error => alert('❌ ' + error.message));
}

// ==================== 5. 表單處理與動態邏輯 (Form Logic) ====================

// 紀錄目前 Modal 操作中的資料狀態
var currentEditId = null; 
var currentIsOngoing = false; 
var currentIsLocked = false; 

// 切換表單欄位是否為唯讀(Disabled)狀態
function setFormDisabled(disabled) {
    var fields = ['discount_name', 'status', 'start_date', 'end_date', 'discount_type_id', 'discount_description', 'is_member', 'minimum_purchase_amount', 'discount_value'];
    fields.forEach(f => {
        var el = document.getElementById(f);
        if(el) {
            el.disabled = disabled;
            el.style.display = ''; // 恢復顯示
            if (f === 'discount_value') {
                var group = el.closest('.input-group');
                if(group) group.style.display = '';
            }
        }
        // 移除暫時產生的純文字顯示標籤
        var textEl = document.getElementById(f + '_text');
        if(textEl) textEl.remove();
    });
    
    // 清除進行中的警告橫幅
    var alertDiv = document.getElementById('ongoingAlert');
    if(alertDiv) alertDiv.remove();
}

// 動態配置 Modal 底部的按鈕（依據檢視/編輯/新增模式切換）
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
    document.getElementById('formModalTitle').innerHTML = '✨ 新增優惠活動';
    
    var form = document.getElementById('activityForm');
    form.reset();
    
	//精準清除所有帶有 .is-invalid 的紅框
	if (form) {
	    form.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
	}
    

    setFormDisabled(false);
    document.getElementById('dynamicFieldsArea').style.display = 'none';
    document.getElementById('charCount').textContent = '0';
    
    // 限制開始日期最早只能從今天開始
    var today = new Date().toISOString().split('T')[0];
    document.getElementById('start_date').min = today;
    
    setupFooterButtons('add'); 
    document.querySelector('#rules-tab').click(); // 強制跳轉至第一頁籤
    modal.show(); 
}

// 點擊列表後開啟檢視視窗，填入該活動的詳細資料
function viewAndEditActivity(id, isOngoing, isLocked) {
    var data = tableData.find(d => d.id === id);
    if (!data) return;

    currentEditId = id; 
    currentIsOngoing = isOngoing;
    currentIsLocked = isLocked; 

    // 依據是否鎖定切換標題與按鈕
    document.getElementById('formModalTitle').innerHTML = isLocked ? '📜 查看活動歷史 (唯讀)' : '📁 查看與修改活動';
    
    // ==================== 【精準修復】：將資料真正填入表單中 ====================
    var form = document.getElementById('activityForm');
    
    // 1. 先清除殘留的紅框
    if(form) form.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
    
    // 2. 依序填入基本資料
    document.getElementById('discount_name').value = data.name;
    document.getElementById('status').value = data.status;
    document.getElementById('start_date').value = data.start;
    document.getElementById('end_date').value = data.end;
    document.getElementById('discount_description').value = data.desc;
    document.getElementById('minimum_purchase_amount').value = data.min;
    document.getElementById('is_member').value = data.isMember.toString();
    
    // 3. 填入折扣類型，並手動觸發 UI 切換 (% 或 元)
    var typeSelect = document.getElementById('discount_type_id');
    typeSelect.value = data.type;
    renderDynamicFields(); // 🌟 這一行超重要，有呼叫它下面的格子才會正確跑出來！

    // 4. 填入折扣值 (處理打折的數學轉換：後端 0.85 -> 前端顯示 85)
    var selectedKeyword = typeSelect.selectedOptions[0] ? typeSelect.selectedOptions[0].getAttribute('data-keyword') : '';
    if (selectedKeyword === 'PERCENT') {
        // 百分比要乘回 100
        document.getElementById('discount_value').value = Math.round(data.val * 100);
    } else {
        document.getElementById('discount_value').value = data.val;
    }
    // =========================================================================

    setFormDisabled(true); 
    setupFooterButtons('view', isLocked); 
    updateCharCount();
    document.querySelector('#rules-tab').click();
    getFormModal().show();
}

// 將檢視視窗切換為編輯模式，並對「進行中」活動實施欄位保護
function enableEditMode() {
    document.getElementById('formModalTitle').innerHTML = '✏️ 修改優惠活動';
    setFormDisabled(false); 

    var today = new Date().toISOString().split('T')[0];

    // 關鍵邏輯：若活動正在進行，則鎖死影響金額計算的關鍵欄位
    if (currentIsOngoing) {
        var form = document.getElementById('activityForm');
        var alertDiv = document.createElement('div');
        alertDiv.id = 'ongoingAlert';
        alertDiv.className = 'alert alert-warning border-0 shadow-sm mb-4 d-flex align-items-center';
        alertDiv.innerHTML = `
            <i class="fa-solid fa-circle-info fs-4 me-3"></i>
            <div><strong>這是一個進行中的活動</strong>：涉及金額計算與開始時間的欄位已自動鎖定。</div>
        `;
        form.prepend(alertDiv);

        // 針對特定欄位進行隱藏，並以純文字 Span 替代顯示
        var lockedFields = ['start_date', 'discount_type_id', 'discount_value', 'minimum_purchase_amount', 'is_member'];
        lockedFields.forEach(fid => {
            var el = document.getElementById(fid);
            if (!el) return;
            el.disabled = true;
            // (此處省略了與之前相同的 span 插入邏輯，建議參考你提供的原始碼中 lockedFields 迴圈部分)
        });
    }

    setupFooterButtons('edit'); 
}

// 當點擊取消修改時，重新以檢視模式開啟該筆資料
function cancelEditMode() {
    viewAndEditActivity(currentEditId, currentIsOngoing, currentIsLocked);
}

function renderDynamicFields() {
    var selectEl = document.getElementById('discount_type_id');
    var selectedOption = selectEl.options[selectEl.selectedIndex];
    var area = document.getElementById('dynamicFieldsArea');
    var addon = document.getElementById('valueAddon');
    var input = document.getElementById('discount_value');
    var hint = document.getElementById('valueHint');

    if(!selectedOption || !selectedOption.value) { area.style.display = 'none'; return; }
    
    area.style.display = 'block';
    var keyword = selectedOption.getAttribute('data-keyword');

    // 切換 % 或是 元 的顯示文字
    if(keyword === 'PERCENT') {
        addon.innerText = '%'; 
        input.max = 99; // 打折不能超過 100%
        
        // 把提示文字變短，並塞進輸入框的 placeholder 裡面
        input.placeholder = '輸入 85 打8.5折';
        if(hint) hint.innerText = ''; // 清空原本下方的提示文字
        
    } else {
        addon.innerText = '元'; 
        input.removeAttribute('max');
        
        // 把提示文字變短，並塞進輸入框的 placeholder 裡面
        input.placeholder = '輸入 100 折扣100元';
        if(hint) hint.innerText = ''; // 清空原本下方的提示文字
    }
}

// 同步更新結束日期的最小值，確保結束不會早於開始
function updateEndDateMin() { 
    document.getElementById('end_date').min = document.getElementById('start_date').value; 
}

// 即時計算說明文字的長度並顯示於 UI 上
function updateCharCount() { 
    document.getElementById('charCount').textContent = document.getElementById('discount_description').value.length; 
}

// 執行儲存動作（包含新增 POST 或 修改 PUT）
function saveActivity() {
    var saveBtn = document.getElementById('saveBtn');
    var form = document.getElementById('activityForm');

    // 1. 每次按下按鈕時，先清除畫面上所有舊的紅框
    if (form) {
        form.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
    }

    // 2. 檢查表單是否有漏填
    if (form && !form.checkValidity()) {
        // 【精準打擊】：只抓出「沒填寫」的輸入框與下拉選單，單獨幫它們加上紅框 class (這樣就不會有綠色打勾)
        form.querySelectorAll('input:invalid, select:invalid, textarea:invalid').forEach(el => {
            el.classList.add('is-invalid');
        });
        
        // 觸發原生提示泡泡：「請填寫這個欄位」
        form.reportValidity();
        return;
    }

    // 禁用按鈕並顯示處理中動畫，防止重複提交
    saveBtn.disabled = true;
    saveBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> 處理中...';

    // 準備傳送至後端的 JSON Payload 資料
    var payload = {
       discountName: document.getElementById('discount_name').value.trim(),
        status: document.getElementById('status').value,
        startDate: document.getElementById('start_date').value,
        endDate: document.getElementById('end_date').value,
        discountDescription: document.getElementById('discount_description').value,
        discountValue: parseFloat(document.getElementById('discount_value').value) || 0,
        minimumPurchaseAmount: parseFloat(document.getElementById('minimum_purchase_amount').value) || 0,
        isMember: document.getElementById('is_member').value === 'true',
        discountType: { discountTypeId: parseInt(document.getElementById('discount_type_id').value) }
    };

    // 如果是百分比模式，須將數值轉為小數 (例如 85% 轉為 0.85) 存入資料庫
    var selectedKeyword = document.getElementById('discount_type_id').selectedOptions[0].getAttribute('data-keyword');
    if (selectedKeyword === 'PERCENT') payload.discountValue /= 100;

    var apiUrl = currentEditId ? `/api/discounts/${currentEditId}` : '/api/discounts';
    var httpMethod = currentEditId ? 'PUT' : 'POST';

    // 發送連線請求
    fetch(apiUrl, {
        method: httpMethod,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
    .then(response => {
        if (!response.ok) throw new Error('伺服器錯誤');
        return response.json();
    })
    .then(data => {
        alert(` 資料已成功${currentEditId ? '修改' : '新增'}！`);
        getFormModal().hide(); 
        fetchDiscountList(); // 成功後自動刷新清單
    })
    .catch(error => {
        alert(' 處理失敗：' + error.message);
        saveBtn.disabled = false;
        saveBtn.innerHTML = currentEditId ? '確定' : '儲存並發布';
    });
}