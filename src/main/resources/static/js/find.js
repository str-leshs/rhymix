document.addEventListener('DOMContentLoaded', () => {
    const findPwForm = document.getElementById('findPwForm');
    const findIdForm = document.getElementById('findIdForm');

    // 📌 비밀번호 찾기 제출 이벤트
    if (findPwForm) {
        findPwForm.addEventListener('submit', function (e) {
            e.preventDefault();

            const username = document.getElementById('username').value.trim();  // 이름
            const nickname = document.getElementById('nickname').value.trim();  // 아이디
            const email = document.getElementById('email').value.trim();

            if (!username || !nickname || !email) {
                showToast('⚠ 이름, 아이디, 이메일을 모두 입력해 주세요.');
                return;
            }

            if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                showToast('⚠ 올바른 이메일 형식을 입력해주세요.');
                return;
            }

            // 서버에 비밀번호 찾기 요청
            axios.post('/api/users/find-password', {
                username,
                nickname,
                email
            })
                .then(() => {
                    showToast('✅ 사용자 인증에 성공했어요! 비밀번호를 재설정해 주세요.');
                    document.getElementById('resetModal').classList.remove('hidden');
                    window.foundNickname = nickname;
                })
                .catch(() => {
                    showToast('❌ 일치하는 계정을 찾을 수 없어요.');
                });
        });

        // 비밀번호 재설정 모달창 핸들링
        document.getElementById('closeModal').addEventListener('click', () => {
            document.getElementById('resetModal').classList.add('hidden');
        });

        document.getElementById('resetBtn').addEventListener('click', () => {
            const pw1 = document.getElementById('newPassword').value;
            const pw2 = document.getElementById('confirmPassword').value;

            if (!pw1 || !pw2) {
                showToast('⚠ 비밀번호를 모두 입력해 주세요.');
                return;
            }

            if (pw1 !== pw2) {
                showToast('❌ 두 비밀번호가 일치하지 않아요.');
                return;
            }

            axios.post('/api/users/reset-password', {
                nickname: window.foundNickname,
                newPassword: pw1
            })
                .then(() => {
                    showToast('✅ 비밀번호가 성공적으로 변경되었어요!');
                    document.getElementById('resetModal').classList.add('hidden');
                })
                .catch(() => {
                    showToast('❌ 비밀번호 변경에 실패했어요.');
                });
        });
    }

    // 📌 아이디 찾기 제출 이벤트
    if (findIdForm) {
        findIdForm.addEventListener('submit', function (e) {
            e.preventDefault();

            const username = document.getElementById('username').value.trim();  // 이름
            const email = document.getElementById('email').value.trim();

            if (!username || !email) {
                showToast('⚠ 이름과 이메일을 모두 입력해 주세요.');
                return;
            }

            if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                showToast('⚠ 올바른 이메일 형식을 입력해주세요.');
                return;
            }

            axios.post('/api/find-id', { username, email })
                .then(res => {
                    const nickname = res.data.nickname;
                    showToast(`✅ 입력한 정보로 가입된 아이디는 ${nickname}입니다.`);
                })
                .catch(() => {
                    showToast('❌ 일치하는 계정을 찾을 수 없어요.');
                });
        });
    }
});

// ✅ 토스트 메시지 함수
function showToast(message) {
    let toast = document.getElementById('toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'toast';
        toast.className = 'toast';
        document.body.appendChild(toast);
    }

    toast.textContent = message;
    toast.classList.remove('hidden');
    toast.classList.add('show');

    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => {
            toast.classList.add('hidden');
        }, 300);
    }, 2000);
}

