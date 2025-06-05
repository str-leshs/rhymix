document.addEventListener("DOMContentLoaded", function () {
    // 약관 동의 페이지 확인
    const agreementPage = document.getElementById("agreement-page");

    if (agreementPage) {
        const requiredChecks = document.querySelectorAll(".required-check");
        const nextBtn = document.getElementById("nextButton");

        requiredChecks.forEach((checkbox) => {
            checkbox.addEventListener("change", updateButtonState);
        });

        function updateButtonState() {
            const allChecked = Array.from(requiredChecks).every(cb => cb.checked);
            nextBtn.disabled = !allChecked;
        }

        updateButtonState();
    }

    // 정보 입력 페이지 로직
    const form = document.querySelector("form");
    if (!form) return;

    const usernameInput = document.getElementById("username"); // 아이디
    const emailInput = document.getElementById("email");
    const password = document.getElementById("password");
    const passwordConfirm = document.getElementById("passwordConfirm");

    const checkButton = document.querySelector(".check-button");

    const usernameError = document.getElementById("usernameError");
    const passwordError = document.getElementById("passwordError");
    const emailError = document.getElementById("emailError");

    // 아이디 유효성 검사
    function isValidNickname(nickname) {
        return /^[a-zA-Z0-9]+$/.test(nickname);
    }

    // 이메일 유효성 검사
    function isValidEmail(email) {
        const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return pattern.test(email);
    }

    // 아이디 중복 확인
    checkButton.addEventListener("click", async function () {
        const nickname = usernameInput.value.trim();

        if (!nickname) {
            alert("아이디를 입력해주세요.");
            return;
        }

        if (!isValidNickname(nickname)) {
            alert("아이디는 영문자와 숫자만 입력 가능합니다.");
            return;
        }

        try {
            const res = await fetch(`/api/users/${nickname}`);
            if (res.ok) {
                alert("이미 존재하는 아이디입니다.");
                usernameInput.value = "";
                usernameInput.classList.remove("valid");
            } else {
                usernameInput.classList.add("valid");
            }
        } catch (err) {
            console.error("중복 확인 실패:", err);
            alert("중복 확인 중 오류가 발생했습니다.");
        }
    });

    // 아이디 입력 시 유효성 메시지 표시
    usernameInput.addEventListener("input", function () {
        const value = usernameInput.value.trim();
        if (value && !isValidNickname(value)) {
            usernameError.style.display = "block";
            usernameInput.classList.remove("valid");
        } else {
            usernameError.style.display = "none";
        }
    });

    // 이메일 입력 시 유효성 메시지 표시
    emailInput.addEventListener("input", function () {
        if (emailInput.value.trim() && !isValidEmail(emailInput.value.trim())) {
            emailError.style.display = "block";
        } else {
            emailError.style.display = "none";
        }
    });

    // 비밀번호 일치 확인
    function checkPasswordMatch() {
        if (password.value && passwordConfirm.value) {
            passwordError.style.display = (password.value !== passwordConfirm.value) ? "block" : "none";
        } else {
            passwordError.style.display = "none";
        }
    }

    password.addEventListener("input", checkPasswordMatch);
    passwordConfirm.addEventListener("input", checkPasswordMatch);

    // 폼 제출 처리
    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        if (!usernameInput.classList.contains("valid")) {
            alert("아이디 중복 확인을 해주세요.");
            return;
        }

        if (!isValidEmail(emailInput.value.trim())) {
            emailError.style.display = "block";
            alert("이메일 형식을 확인해주세요.");
            return;
        }

        if (password.value !== passwordConfirm.value) {
            alert("비밀번호가 일치하지 않습니다.");
            return;
        }

        const data = {
            username: document.getElementById("name").value,
            nickname: usernameInput.value,
            email: emailInput.value,
            password: password.value,
            confirmPassword: passwordConfirm.value
        };

        try {
            const res = await fetch("/api/users/signup", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            });

            if (res.ok) {
                alert("환영합니다! 로그인 후 블로그 기본세팅을 진행해주세요(마이페이지 → 프로필 편집)");
                window.location.href = "/login";
            } else {
                const message = await res.text();
                alert(`회원가입 실패: ${message}`);
            }
        } catch (err) {
            console.error("회원가입 오류:", err);
            alert("회원가입 중 오류가 발생했습니다.");
        }
    });
});

