// 預覽上傳圖片
function PetPreviewImage(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = e => {
            const preview = document.getElementById("preview");
            if (preview) {
                preview.src = e.target.result;
                preview.style.display = "block";
            }
        };
        reader.readAsDataURL(file);
    }
}

// 載入寵物列表分頁
function PetLoadPage(page) {
    fetch('/pets/admin/list?page=' + page)
        .then(response => response.text())
        .then(html => {
            const container = document.querySelector('#contentBody');
            if (container) {
                container.innerHTML = html;
            }
        })
        .catch(() => alert("載入寵物列表失敗"));
}

// 提交編輯表單（AJAX）
function PetSubmitUpdate(formId) {
    const form = document.getElementById(formId || "editForm");
    if (!form) {
        alert("找不到編輯表單");
        return;
    }

    const formData = new FormData(form);
    fetch(form.action, {
        method: "POST",
        body: formData
    })
    .then(response => response.text())
    .then(html => {
        const container = document.querySelector('#contentBody');
        if (container) {
            container.innerHTML = html;
        }
        alert("寵物資料更新成功 🐾");
    })
    .catch(() => alert("更新失敗，請稍後再試"));
}
function initPetTableEvents() {
    // 修改
    document.querySelectorAll(".edit-btn").forEach(btn => {
        btn.onclick = function() {
            const row = this.closest("tr");
            const inputs = row.querySelectorAll("input");
            const selects = row.querySelectorAll("select");
            const petId = row.dataset.petId;

            if (this.textContent === "修改") {
                inputs.forEach(input => input.readOnly = false);
                selects.forEach(select => select.disabled = false);
                this.textContent = "保存";
            } else {
                const formData = new FormData();
                formData.set("action", "update");
                formData.set("source", "backend");
                formData.set("pet_id", petId);

                inputs.forEach(input => formData.set(input.name, input.value));
                selects.forEach(select => formData.set(select.name, select.value));

                fetch(`${contextPath}/PetServlet`, { method: "POST", body: formData })
                  .then(resp => resp.text())
                  .then(html => {
                      document.getElementById("contentBody").innerHTML = html;
                      initPetTableEvents(); // 再次綁定
                  });
                inputs.forEach(input => input.readOnly = true);
                selects.forEach(select => select.disabled = true);
                this.textContent = "修改";
            }
        };
    });
	// 刪除
	document.querySelectorAll(".delete-btn").forEach(btn => {
	    btn.onclick = function() {
	        const petId = this.dataset.petId;

	        // 先詢問使用者
	        if (!confirm("確定要刪除這筆寵物資料嗎？")) {
	            return; // 使用者按取消 → 不執行
	        }

	        const formData = new FormData();
	        formData.set("action", "delete");
	        formData.set("source", "backend");
	        formData.set("pet_id", petId);

	        fetch(`${contextPath}/PetServlet`, { method: "POST", body: formData })
	          .then(resp => resp.text())
	          .then(html => {
	              document.getElementById("contentBody").innerHTML = html;
	              initPetTableEvents(); // 再次綁定
	          });
	    };
	});
	
	
}
/* --- 訂單管理專用邏輯 --- */

/* --- 儲存功能修正版 --- */
function handleOrderEdit(btn, orderId) {
    const row = document.getElementById(`row-${orderId}`);
    const fields = row.querySelectorAll('.edit-input-orange');
    
    if (btn.innerText === "修改") {
        fields.forEach(f => f.disabled = false);
        btn.innerText = "儲存";
        btn.classList.replace("btn-orange-action", "btn-success");
        row.style.backgroundColor = "#FFFDE7"; 
    } else {
        // 確保這兩行有抓到值
        const status = row.querySelector('[data-field="orderStatus"]').value;
        const payment = row.querySelector('[data-field="orderPayment"]').value;

        // 使用絕對路徑 /api/...
        fetch(`/api/order/updateOrder/${orderId}`, {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Accept': 'application/json' 
            },
            body: JSON.stringify({ 
                orderStatus: status, 
                orderPayment: payment 
            })
        })
        .then(res => {
            if (res.ok) {
                alert('修改成功！');
                fields.forEach(f => f.disabled = true);
                btn.innerText = "修改";
                btn.classList.replace("btn-success", "btn-orange-action");
                row.style.backgroundColor = ""; 
            } else {
                // 如果失敗，印出狀態碼看看是 403 (權限) 還是 400 (格式錯誤)
                console.error("儲存失敗，狀態碼:", res.status);
                alert('儲存失敗，狀態碼: ' + res.status);
            }
        })
        .catch(err => console.error("Fetch 錯誤:", err));
    }
}

/* --- 2. 處理刪除 --- */
function handleOrderDelete(orderId) {
    if (confirm(`確定要刪除訂單 #${orderId} 嗎？`)) {
		fetch(`/api/order/deleteOrder/${orderId}`, {
		    method: 'DELETE'
		})
        .then(res => {
            if (res.ok) {
                const row = document.getElementById(`row-${orderId}`);
                row.style.opacity = "0";
                setTimeout(() => row.remove(), 500);
                alert('刪除成功');
            } else {
                alert('刪除失敗');
            }
        });
    }
}

/*--------------*/ 
