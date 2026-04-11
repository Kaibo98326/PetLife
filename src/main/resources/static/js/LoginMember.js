const container = document.getElementById('container');
const registerBtn = document.getElementById('register');
const loginBtn = document.getElementById('login');

registerBtn.addEventListener('click', () => {
    container.classList.add("active");
});
loginBtn.addEventListener('click', () => {
    container.classList.remove("active");
});

const form = document.getElementById("registerForm");
if (form) {
    const inputs = form.querySelectorAll("input");

    // blur 驗證
    inputs.forEach(input => {
        input.addEventListener("blur", function () {
            if (this.value.trim() === "") {
                this.classList.add("input-error");
                switch (this.name) {
                    case "member_name": this.placeholder = "請輸入姓名"; break;
                    case "phone": this.placeholder = "請輸入電話"; break;
                    case "email": this.placeholder = "請輸入電子郵件"; break;
                    case "password": this.placeholder = "請輸入密碼"; break;
                    case "address": this.placeholder = "請輸入地址"; break;
                }
            } else {
                this.classList.remove("input-error");
                switch (this.name) {
                    case "member_name": this.placeholder = "姓名"; break;
                    case "phone": this.placeholder = "電話"; break;
                    case "email": this.placeholder = "電子郵件"; break;
                    case "password": this.placeholder = "密碼"; break;
                    case "address": this.placeholder = "地址"; break;
                }
            }
        });

        // 即時輸入移除錯誤提示
        input.addEventListener("input", function () {
            if (this.value.trim() !== "") {
                this.classList.remove("input-error");
            }
        });
    });

    // 送出表單時檢查
    form.addEventListener("submit", function (event) {
        let valid = true;
        inputs.forEach(input => {
            if (input.value.trim() === "") {
                input.classList.add("input-error");
                switch (input.name) {
                    case "member_name": input.placeholder = "請輸入姓名"; break;
                    case "phone": input.placeholder = "請輸入電話"; break;
                    case "email": input.placeholder = "請輸入電子郵件"; break;
                    case "password": input.placeholder = "請輸入密碼"; break;
                    case "address": input.placeholder = "請輸入地址"; break;
                }
                valid = false;
            }
        });
        if (!valid) {
            event.preventDefault(); // 阻止送出
        }
    });
}
