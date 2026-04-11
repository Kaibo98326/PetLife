// 美容項目／預約內嵌用。AdminDashboard.jsp 最後載入；須先有 loadContentViaAjax、extractBodyContent、executeScripts

// --- 用網址載入右邊那一塊（GET），列表查詢／修改連結會用到 ---
function beautyLoadContentByUrl(title, url) {
    document.getElementById('contentTitle').textContent = title;
    document.getElementById('breadcrumb').textContent = title;
    loadContentViaAjax(url, title);
}

// --- 表單送出：不要整頁跳走，用 fetch POST；return false 取消預設 submit ---
function beautyPostFormThenShow(form) {
    var el = document.getElementById('contentBody');
    // 有 name="action" 的欄位時，form.action 可能是 input，故用 getAttribute('action')
    var url = form.getAttribute('action') || (typeof form.action === 'string' ? form.action : location.href);

    fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
        body: new URLSearchParams(new FormData(form))
    })
        .then(function (r) { return r.text(); })
        .then(function (html) {
            el.innerHTML = extractBodyContent(html);
            executeScripts(el);
        })
        .catch(function () {
            el.innerHTML = '<p class="error-message">送出失敗</p>';
        });

    return false;
}
