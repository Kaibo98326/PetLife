
// 攔截表單送出，改用 AJAX
function empPostFormThenShow(form, title, module) {
    const el = document.getElementById('contentBody');
    const url = form.getAttribute('action');

    fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
        body: new URLSearchParams(new FormData(form))
    })
    .then(r => r.text())
    .then(html => {
        el.innerHTML = extractBodyContent(html);
        executeScripts(el);
        document.getElementById('contentTitle').textContent = title;
        document.getElementById('breadcrumb').textContent = title;
    })
    .catch(() => {
        el.innerHTML = '<p class="error-message">送出失敗</p>';
    });

    return false; // 阻止瀏覽器跳轉
}
function togglePasswordField() {
    const field = document.getElementById('passwordField');
    if (field) {
        field.style.display = (field.style.display === 'none') ? 'block' : 'none';
    }
}
function searchEmpByName() {
    const keyword = document.getElementById("searchEmpName").value.trim();
    if (!keyword) {
        loadContent('員工列表', 'empList');
        return;
    }
    loadContent('員工列表', 'empSearch&empName=' + encodeURIComponent(keyword));
}
function submitNewMember() {
            const form = document.getElementById("addMemberForm");
            const formData = {
                memberName: form.memberName.value,
                phone: form.phone.value,
                email: form.email.value,
                passwordHash: form.passwordHash.value,
                address: form.address.value,
                accountStatus: form.status.value
            };

            fetch(form.action, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData)
            })
            .then(response => {
                if (response.ok) {
                    alert("會員新增成功");
                    loadMemberList(); // 新增成功後回到列表
                } else {
                    alert("新增失敗");
                }
            })
            .catch(() => alert("系統錯誤，請稍後再試"));
        }

        function loadMemberList() {
            fetch('/members/list?page=0')
                .then(response => response.text())
                .then(html => {
                    document.querySelector("#addFormFragment").outerHTML = html;
                });
        }























