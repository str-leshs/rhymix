document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.querySelector(".search-input");
    const searchButton = document.querySelector("#search-trigger");
    const resultContainer = document.querySelector(".friend-list");
    const genreSelect = document.querySelector("#genre-select");

    // 🔍 검색 버튼 클릭 시 동작
    searchButton.addEventListener("click", () => {
        const keyword = searchInput.value.trim();
        const selectedGenre = genreSelect.value;
        if (!keyword && !selectedGenre) {
            showToastModal({ success: false, message: "검색어 또는 장르를 선택하세요." });
            return;
        }

        fetch(`/api/search-neighbors?keyword=${encodeURIComponent(keyword)}&genre=${encodeURIComponent(selectedGenre)}`)
            .then(res => res.json())
            .then(users => {
                resultContainer.innerHTML = "";

                if (users.length === 0) {
                    resultContainer.innerHTML = "<p>🔍 검색 결과가 없습니다.</p>";
                    showToastModal({ success: false, message: "검색 결과가 없습니다." });
                    return;
                }

                users.forEach(user => {
                    const userBox = document.createElement("div");
                    userBox.classList.add("friend-item");

                    userBox.innerHTML = `
                        <div class="user-section">
                            <input type="checkbox" data-username="${user.username}" />
                            <img src="${user.profileImage || '/image/placeholder_circle.png'}" class="profile-img" />
                            <div class="nickname">${user.nickname}</div>
                            <div class="bio">${user.bio || "상태 메시지가 없습니다."}</div>
                            <div class="tags">
                                ${(user.preferredGenres || []).map(tag => `<span class="tag"># ${tag}</span>`).join('')}
                            </div>
                        </div>
                        <div class="friend-actions">
                            <button class="add-neighbor-btn" data-nickname="${user.nickname}">이웃 신청</button>
                        </div>
                    `;

                    resultContainer.appendChild(userBox);
                });

                // ✅ 동적으로 생성된 버튼에 이벤트 다시 연결
                bindAddNeighborButtons();
            })
            .catch(err => {
                console.error("검색 오류:", err);
                resultContainer.innerHTML = "<p>⚠️ 검색 중 오류가 발생했습니다.</p>";
                showToastModal({ success: false, message: "검색 중 오류가 발생했습니다." });
            });
    });

    // ✅ 동적으로 생성된 이웃신청 버튼에 이벤트 연결
    function bindAddNeighborButtons() {
        const buttons = document.querySelectorAll(".add-neighbor-btn");

        buttons.forEach(button => {
            button.addEventListener("click", () => {
                const targetNickname = button.dataset.nickname;

                if (!targetNickname) {
                    showToastModal({ success: false, message: "닉네임 정보가 없습니다." });
                    return;
                }

                // 🔐 모달의 data-nickname에 저장
                const modal = document.getElementById('custom-confirm');
                modal.dataset.nickname = targetNickname;

                // 모달 메시지 설정 및 열기
                document.getElementById('confirm-title').textContent = "이웃 신청 확인";
                document.getElementById('confirm-message').textContent = `${targetNickname}님에게 이웃 신청하시겠습니까?`;
                modal.classList.remove('hidden');
            });
        });
    }

    // ✅ 모달 버튼 처리
    const confirmOkBtn = document.getElementById('confirm-ok');
    const confirmCancelBtn = document.getElementById('confirm-cancel');

    confirmOkBtn.addEventListener("click", () => {
        const modal = document.getElementById('custom-confirm');
        const nickname = modal.dataset.nickname;

        modal.classList.add('hidden');

        fetch("/api/neighbors/apply", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ nickname })
        })
            .then(res => {
                if (res.ok) {
                    showToastModal({ success: true, message: `${nickname}님에게 이웃 신청 완료!` });
                } else {
                    showToastModal({ success: false, message: `${nickname}님 신청 실패 😢` });
                }
            })
            .catch(err => {
                console.error(err);
                showToastModal({ success: false, message: `${nickname}님 신청 중 오류 발생` });
            });
    });

    confirmCancelBtn.addEventListener("click", () => {
        document.getElementById('custom-confirm').classList.add('hidden');
    });

    // ✅ 토스트 알림 함수
    function showToastModal({ success, message }) {
        const modal = document.getElementById("toast-modal");
        const icon = document.getElementById("toast-icon");
        const title = document.getElementById("toast-title");
        const msg = document.getElementById("toast-message");

        icon.textContent = success ? "✅" : "❌";
        title.textContent = success ? "성공!" : "실패!";
        msg.textContent = message;

        modal.classList.remove("hidden");

        setTimeout(() => {
            modal.classList.add("hidden");
        }, 2000);
    }
});
