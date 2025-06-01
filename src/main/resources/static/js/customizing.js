document.addEventListener('DOMContentLoaded', () => {
    initThemeSelection();
    initSaveButton();
    loadSavedTheme();
    const username = document.getElementById("hidden-username")?.value;
    const nickname = document.getElementById("hidden-nickname")?.value;
    console.log("📌 username:", username);
    console.log("📌 nickname:", nickname);
    loadLatestPost();
});

function initThemeSelection() {
    const themeBoxes = document.querySelectorAll('.theme-box');
    themeBoxes.forEach(box => {
        box.addEventListener('click', () => {
            themeBoxes.forEach(b => b.classList.remove('selected'));
            box.classList.add('selected');
        });
    });
}

function initSaveButton() {
    const saveBtn = document.querySelector('.save-btn');
    if (!saveBtn) return;

    saveBtn.addEventListener('click', () => {
        const selectedBox = document.querySelector('.theme-box.selected');
        if (!selectedBox) {
            Swal.fire({
                icon: 'warning',
                title: '선택된 테마가 없습니다.',
                text: '테마를 먼저 선택해주세요.',
                confirmButtonText: '확인'
            });
            return;
        }

        const selectedTheme = selectedBox.dataset.theme;
        if (!selectedTheme) {
            Swal.fire({
                icon: 'error',
                title: '테마 정보 없음',
                text: '선택된 테마에 데이터가 없습니다.',
                confirmButtonText: '확인'
            });
            return;
        }

        saveTheme(selectedTheme);
    });
}

function saveTheme(themeName) {
    const username = document.getElementById('hidden-username')?.value;
    const titleEl = document.getElementById('title-input');
    const commentEl = document.getElementById('content-input');

    if (!commentEl) {
        console.warn("❌ content-input 요소를 찾을 수 없습니다.");
    }

    const title = titleEl?.value?.trim();
    const comment = commentEl?.innerText?.trim();
    console.log("🎯 저장 전 comment:", comment);

    if (!username) {
        Swal.fire({
            icon: 'error',
            title: '저장 실패!',
            text: 'username이 정의되지 않았습니다.',
            confirmButtonText: '확인'
        });
        return;
    }

    fetch('/api/users/me/theme', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ theme: themeName })
    })
        .then(res => {
            if (!res.ok) throw new Error('테마 저장 실패');
            return res.text();
        })
        .then(() => {
            const postDto = {
                userId: username,
                title: title || '(제목 없음)',
                artist: '미입력',
                cover: '',
                weather: '',
                mood: '',
                comment: comment || '(내용 없음)'
            };

            return fetch('/api/posts', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(postDto)
            });
        })
        .then(res => {
            if (!res.ok) throw new Error('글 저장 실패');
            return res.json();
        })
        .then(() => {
            Swal.fire({
                icon: 'success',
                title: '테마와 일기가 저장되었습니다!',
                text: '메인 페이지로 이동합니다.',
                confirmButtonText: '확인'
            }).then(() => {
                window.location.href = '/main';
            });
        })
        .catch((err) => {
            Swal.fire({
                icon: 'error',
                title: '저장 실패!',
                text: err.message || '저장 중 오류가 발생했습니다.',
                confirmButtonText: '확인'
            });
        });
}


function loadLatestPost() {
    fetch('/api/posts/my-latest')
        .then(res => res.json())
        .then(post => {
            const titleInput = document.getElementById('title-input');
            const commentInput = document.getElementById('content-input');

            if (titleInput) titleInput.value = post.title || '(제목 없음)';
            if (commentInput) commentInput.innerText = post.comment || '(내용 없음)';
        });
}

function loadSavedTheme() {
    fetch('/api/auth/me')
        .then(res => {
            if (!res.ok) throw new Error('정보 불러오기 실패');
            return res.json();
        })
        .then(user => {
            const currentTheme = user.selectedTheme;
            if (!currentTheme) return;
            const currentBox = document.querySelector(`.theme-box[data-theme="${currentTheme}"]`);
            if (currentBox) currentBox.classList.add('selected');
        })
        .catch(err => {
            console.warn(" 불러오기 실패:", err);
        });
}
