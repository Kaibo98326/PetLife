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