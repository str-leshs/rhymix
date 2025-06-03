document.addEventListener('DOMContentLoaded', () => {
    initThemeSelection();
    initSaveButton();
    loadSavedDiary();
    const username = document.getElementById("hidden-username")?.value;
    const nickname = document.getElementById("hidden-nickname")?.value;
    console.log("📌 username:", username);
    console.log("📌 nickname:", nickname);

    document.getElementById("imageInput").addEventListener("change", handleImageUpload);

    setTimeout(() => {
        loadSavedTheme();
    }, 0);
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
            Swal.fire({ icon: 'warning', title: '선택된 테마가 없습니다.', text: '테마를 먼저 선택해주세요.' });
            return;
        }

        const selectedTheme = selectedBox.dataset.theme;
        if (!selectedTheme) {
            Swal.fire({ icon: 'error', title: '테마 정보 없음', text: '선택된 테마에 데이터가 없습니다.' });
            return;
        }

        saveTheme(selectedTheme);
    });
}

function saveTheme(themeName) {
    const username = document.getElementById('hidden-username')?.value;
    const titleEl = document.getElementById('title-input');
    const commentEl = document.getElementById('content-input');
    const imageEl = document.querySelector("#imagePreviewContainer img");
    const saveBtn = document.querySelector(".save-btn");

    const title = titleEl?.value?.trim();
    const comment = commentEl?.innerText?.trim();
    const image = imageEl?.src || "";

    if (!username) {
        Swal.fire({ icon: 'error', title: '저장 실패!', text: 'username이 정의되지 않았습니다.' });
        return;
    }

    // 저장 버튼 비활성화
    saveBtn.disabled = true;

    // 1️⃣ 테마 저장
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
            // 2️⃣ 다이어리 저장
            const diaryDto = {
                diaryTitle: title || '(제목 없음)',
                diaryContent: comment || '(내용 없음)',
                diaryImage: image
            };

            return fetch('/api/users/me/diary', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(diaryDto)
            });
        })
        .then(res => {
            if (!res.ok) throw new Error('다이어리 저장 실패');
            return res.text();
        })
        .then(() => {
            Swal.fire({
                icon: 'success',
                title: '테마와 다이어리가 저장되었습니다!',
                text: '메인 페이지로 이동합니다.',
                confirmButtonText: '확인'
            }).then(() => {
                window.location.href = '/main';
            });
        })
        .catch((err) => {
            Swal.fire({ icon: 'error', title: '저장 실패!', text: err.message || '저장 중 오류가 발생했습니다.' });
        })
        .finally(() => {
            saveBtn.disabled = false; // 저장 버튼 재활성화
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

            // ✅ 모든 테마 박스에서 selected 제거
            document.querySelectorAll('.theme-box').forEach(box => {
                box.classList.remove('selected');
            });

            // ✅ 저장된 테마만 selected 추가
            const currentBox = document.querySelector(`.theme-box[data-theme="${currentTheme}"]`);
            if (currentBox) currentBox.classList.add('selected');
        })
        .catch(err => {
            console.warn("불러오기 실패:", err);
        });
}


function loadSavedDiary() {
    fetch("/api/users/me/diary")
        .then(res => {
            if (!res.ok) throw new Error("다이어리 없음");
            return res.json();
        })
        .then(diary => {
            const titleInput = document.getElementById("title-input");
            const contentInput = document.getElementById("content-input");
            const previewContainer = document.getElementById("imagePreviewContainer");
            const filenameLabel = document.getElementById("imageFilename");

            if (titleInput) titleInput.value = diary.diaryTitle || "";
            if (contentInput) contentInput.innerText = diary.diaryContent || "";

            // 이미지 미리보기 반영
            if (diary.diaryImage) {
                previewContainer.innerHTML = `<img src="${diary.diaryImage}" style="max-width: 100%; margin-top: 10px; border-radius: 8px;" />`;
                filenameLabel.textContent = "이미 업로드된 이미지";
            }
        })
        .catch(err => {
            console.warn("📘 다이어리 불러오기 실패:", err);
        });
}


function handleImageUpload(event) {
    const file = event.target.files[0];
    const filenameLabel = document.getElementById("imageFilename");
    const previewContainer = document.getElementById("imagePreviewContainer");

    if (!file) {
        filenameLabel.textContent = "선택된 파일 없음";
        previewContainer.innerHTML = "";
        return;
    }

    // 이미지 형식 검사
    if (!file.type.startsWith("image/")) {
        Swal.fire({ icon: 'error', title: '이미지 형식 오류', text: '이미지 파일만 업로드 가능합니다.' });
        event.target.value = "";
        return;
    }

    // 용량 제한 (1MB 이하)
    const maxSize = 1024 * 1024; // 1MB
    if (file.size > maxSize) {
        Swal.fire({ icon: 'warning', title: '파일 크기 초과', text: '이미지 용량은 1MB 이하만 가능합니다.' });
        event.target.value = "";
        return;
    }

    // 미리보기 및 파일 이름 표시
    const reader = new FileReader();
    reader.onload = (e) => {
        filenameLabel.textContent = file.name;
        previewContainer.innerHTML = `<img src="${e.target.result}" style="max-width: 100%; margin-top: 10px; border-radius: 8px;" />`;
    };
    reader.readAsDataURL(file);
}
