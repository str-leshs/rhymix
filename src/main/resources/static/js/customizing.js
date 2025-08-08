let deleteImage = false;

document.addEventListener('DOMContentLoaded', () => {
    initThemeSelection();
    initSaveButton();
    loadSavedDiary();
    const username = document.getElementById("hidden-username")?.value;
    const nickname = document.getElementById("hidden-nickname")?.value;
    console.log("📌 username:", username);
    console.log("📌 nickname:", nickname);

    document.getElementById("imageInput").addEventListener("change", handleImageUpload);

    // 이미지 삭제 버튼 이벤트
    const deleteBtn = document.getElementById("delete-image-btn");
    if (deleteBtn) {
        deleteBtn.addEventListener("click", () => {
            document.getElementById("imagePreviewContainer").innerHTML = "";
            document.getElementById("imageFilename").textContent = "선택된 파일 없음";
            deleteImage = true;
        });
    }

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

    saveBtn.disabled = true;

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
            const diaryDto = {
                diaryTitle: title || '(제목 없음)',
                diaryContent: comment || '(내용 없음)',
                diaryImage: deleteImage ? null : image,
                deleteImage: deleteImage
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
            saveBtn.disabled = false;
        });
}

function loadSavedTheme() {
    fetch('/api/auth/me')
        .then(res => res.json())
        .then(user => {
            const currentTheme = user.selectedTheme;
            if (!currentTheme) return;
            document.querySelectorAll('.theme-box').forEach(box => box.classList.remove('selected'));
            const currentBox = document.querySelector(`.theme-box[data-theme="${currentTheme}"]`);
            if (currentBox) currentBox.classList.add('selected');
        })
        .catch(err => console.warn("불러오기 실패:", err));
}

function loadSavedDiary() {
    fetch("/api/users/me/diary")
        .then(res => res.json())
        .then(diary => {
            const titleInput = document.getElementById("title-input");
            const contentInput = document.getElementById("content-input");
            const previewContainer = document.getElementById("imagePreviewContainer");
            const filenameLabel = document.getElementById("imageFilename");

            if (titleInput) titleInput.value = diary.diaryTitle || "";
            if (contentInput) contentInput.innerText = diary.diaryContent || "";

            if (diary.diaryImage) {
                previewContainer.innerHTML = `<img src="${diary.diaryImage}" style="max-width: 100%; margin-top: 10px; border-radius: 8px;" />`;
                filenameLabel.textContent = "이미 업로드된 이미지";
                deleteImage = false;
            }
        })
        .catch(err => console.warn("다이어리 불러오기 실패:", err));
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

    if (!file.type.startsWith("image/")) {
        Swal.fire({ icon: 'error', title: '이미지 형식 오류', text: '이미지 파일만 업로드 가능합니다.' });
        event.target.value = "";
        return;
    }

    const maxSize = 1024 * 1024;
    if (file.size > maxSize) {
        Swal.fire({ icon: 'warning', title: '파일 크기 초과', text: '이미지 용량은 1MB 이하만 가능합니다.' });
        event.target.value = "";
        return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
        filenameLabel.textContent = file.name;
        previewContainer.innerHTML = `<img src="${e.target.result}" style="max-width: 100%; margin-top: 10px; border-radius: 8px;" />`;
        deleteImage = false; // 새로 업로드하면 삭제 플래그 해제
    };
    reader.readAsDataURL(file);
}
