// find.js

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('findPwForm') || document.getElementById('findIdForm');

    form.addEventListener('submit', function (e) {
        e.preventDefault();

        // 입력값 가져오기
        const name = document.getElementById('name')?.value.trim();
        const email = document.getElementById('email')?.value.trim();
        const usernameInput = document.getElementById('username'); // 비번 찾기만 존재
        const username = usernameInput ? usernameInput.value.trim() : null;

        // 필수 입력 확인
        if (!name || !email || (usernameInput && !username)) {
            showToast('모든 항목을 입력해주세요.');
            return;
        }

        // 이메일 형식 검사
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            showToast('올바른 이메일 형식을 입력해주세요.');
            return;
        }

        // 추후 서버 요청용 자리
        console.log("✔️ 폼 전송 준비 완료");
        console.log("이름:", name);
        console.log("이메일:", email);
        if (usernameInput) console.log("아이디:", username);

        showToast('✅ 입력 완료. (나중에 서버로 전송 예정)');
    });
});

// 토스트 메시지 함수 (로그인 때와 동일)
function showToast(message) {
    let toast = document.getElementById('toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'toast';
        toast.className = 'toast';
        document.body.appendChild(toast);

        const style = document.createElement('style');
        style.textContent = `
            .toast {
                position: fixed;
                top: 80px;
                left: 50%;
                transform: translateX(-50%);
                background-color: rgba(60, 50, 30, 0.9);
                color: white;
                padding: 10px 20px;
                border-radius: 8px;
                font-size: 14px;
                opacity: 0;
                transition: opacity 0.3s ease-in-out;
                z-index: 9999;
            }
            .toast.show {
                opacity: 1;
            }
        `;
        document.head.appendChild(style);
    }

    toast.textContent = message;
    toast.classList.add("show");

    setTimeout(() => {
        toast.classList.remove("show");
    }, 2500);
    function showToast(message) {
        const toast = document.getElementById('toast');
        toast.textContent = message;
        toast.classList.remove('hidden');
        toast.classList.add('show');

        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => {
                toast.classList.add('hidden');
            }, 300); // 애니메이션 끝나고 숨김
        }, 2000); // 2초간 표시
    }
    document.getElementById("findIdBtn").addEventListener("click", function () {
        const name = document.getElementById("name").value;
        const email = document.getElementById("email").value;

        axios.post("/api/find-id", { name, email })
            .then(res => {
                const userId = res.data.username; // ex: rhymix_user
                showToast(`✨ 입력한 정보로 가입된 아이디는 ${userId}입니다!`);
            })
            .catch(err => {
                showToast("❌ 입력한 정보와 일치하는 계정을 찾을 수 없어요.");
            });
    });

}
