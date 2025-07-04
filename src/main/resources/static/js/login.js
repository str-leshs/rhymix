// login.js
// 1. 폼 제출 이벤트 가로채기
const loginForm = document.getElementById('loginForm');
loginForm.addEventListener('submit', function (e) {
    e.preventDefault(); // 기본 제출 막기
    login();    // 커스텀 로그인 함수 실행
});

// 2. 로그인 함수 정의
function login() {
    const nickname = document.getElementById('nickname').value.trim();
    const password = document.getElementById('password').value.trim();

    console.log("보내는 값:", nickname, password);

    // 3. 입력값 검사
    if (!nickname || !password) {
        showToast('아이디와 비밀번호를 모두 입력해주세요.');
        return;
    }

    // 4. 서버로 로그인 요청 보내기
    axios.post('/api/auth/login', {
        nickname: nickname,
        password: password
    })
        .then(function (response) {
            // 로그인 성공: 메인 페이지로 이동
            window.location.href = '/main';
        })
        .catch(function (error) {
            // 로그인 실패: 경고창 표시
            showToast('로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.');
        });
}

// 5. 커스텀 토스트 메시지 표시 함수
function showToast(message) {
    let toast = document.getElementById('toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'toast';
        toast.className = 'toast';
        document.body.appendChild(toast);
    }
    toast.textContent = message;
    toast.classList.add("show");

    setTimeout(() => {
        toast.classList.remove("show");
    }, 2500);
}
