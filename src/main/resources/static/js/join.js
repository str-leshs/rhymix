// 약관 동의 페이지에만 작동하도록 body id 확인
if (document.getElementById("agreement-page")) {

    // DOM 로드 후 실행
    document.addEventListener("DOMContentLoaded", function () {
        const requiredChecks = document.querySelectorAll(".required-check");
        const nextBtn = document.getElementById("nextButton");

        // 체크박스 상태 변화 감지
        requiredChecks.forEach((checkbox) => {
            checkbox.addEventListener("change", updateButtonState);
        });

        function updateButtonState() {
            const allChecked = Array.from(requiredChecks).every(cb => cb.checked);
            nextBtn.disabled = !allChecked;
        }

        // 초기 상태 설정
        updateButtonState();
    });
}

//TODO 비밀번호와 비밀번호 입력이 다른 경우 경고 문구 출력
document.addEventListener("DOMContentLoaded", function () {
    const password = document.querySelector('input[name="password"]');
    const passwordConfirm = document.querySelector('input[name="passwordConfirm"]');
    const errorText = document.getElementById("passwordError");

    function checkPasswordMatch() {
        // 둘 다 입력되었을 때만 검사
        if (password.value.trim() && passwordConfirm.value.trim()) {
            if (password.value !== passwordConfirm.value) {
                errorText.style.display = "block";
            } else {
                errorText.style.display = "none";
            }
        } else {
            errorText.style.display = "none";
        }
    }

    password.addEventListener("input", checkPasswordMatch);
    passwordConfirm.addEventListener("input", checkPasswordMatch);
});

