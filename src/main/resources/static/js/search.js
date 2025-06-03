let currentPage = 1;
const pageSize = 10;

window.addEventListener("DOMContentLoaded", () => {
    fetchNeighbors();
});

// 돋보기 클릭하거나
document.getElementById("searchButton").addEventListener("click", function () {
    currentPage = 1;
    fetchNeighbors();
});
// 엔터키 누르면 검색 가능
document.getElementById("keywordInput").addEventListener("keydown", function (e) {
    if (e.key === "Enter") {
        e.preventDefault(); // 폼 제출 방지
        currentPage = 1;
        fetchNeighbors();
    }
});
//장르 검색하고 엔터키 누르면 검색 가능
document.getElementById("genreSelect").addEventListener("keydown", function (e) {
    if (e.key === "Enter") {
        e.preventDefault();
        currentPage = 1;
        fetchNeighbors();
    }
});


function fetchNeighbors() {
    const genre = document.getElementById("genreSelect").value;
    const keyword = document.getElementById("keywordInput").value;
    const friendList = document.getElementById("friendList");
    const pagination = document.getElementById("pagination");

    const params = new URLSearchParams();
    params.append("page", currentPage);
    params.append("size", pageSize);
    if (genre) params.append("genre", genre);
    if (keyword) params.append("keyword", keyword);

    fetch(`/api/neighbors/search?${params.toString()}`)
        .then(response => response.json())
        .then(data => {
            friendList.innerHTML = "";
            pagination.innerHTML = "";

            if (data.content.length === 0) {
                friendList.innerHTML = "<p>검색 결과가 없습니다.</p>";
                return;
            }

            data.content.forEach(neighbor => {
                const profile = neighbor.profileImage || '/image/placeholder_circle.png';
                const genres = (neighbor.preferredGenres || []).map(tag => `<span class="tag"># ${tag}</span>`).join("");

                const html = `
                        <div class="friend-item">
                            <input type="checkbox" />
                            <img src="${profile}" class="profile-img" />
                            <div class="nickname">${neighbor.nickname}</div>
                            <div class="tags">${genres}</div>
                        </div>
                    `;
                friendList.insertAdjacentHTML("beforeend", html);
            });

            for (let i = 1; i <= data.totalPages; i++) {
                const pageBtn = document.createElement("button");
                pageBtn.textContent = i;
                pageBtn.className = i === data.currentPage ? "active" : "";
                pageBtn.addEventListener("click", () => {
                    currentPage = i;
                    fetchNeighbors();
                });
                pagination.appendChild(pageBtn);
            }
        })
        .catch(error => {
            console.error("검색 오류:", error);
            friendList.innerHTML = "<p>오류가 발생했습니다.</p>";
        });
}

//커스텀 알림창
function showCustomAlert(message) {
    document.getElementById("alert-message").textContent = message;
    document.getElementById("custom-alert").classList.remove("hidden");
}

document.getElementById("alert-close").addEventListener("click", () => {
    document.getElementById("custom-alert").classList.add("hidden");
});

// 이웃 신청 기능
document.getElementById("request-button").addEventListener("click", async () => {
    const checkboxes = document.querySelectorAll("#friendList input[type='checkbox']:checked");
    if (checkboxes.length === 0) {
        showCustomAlert("신청할 친구를 선택하세요!");
        return;
    }

    const nicknames = [...checkboxes].map(cb => {
        const nicknameElem = cb.closest(".friend-item").querySelector(".nickname");
        return nicknameElem ? nicknameElem.textContent.trim() : null;
    }).filter(Boolean);

    try {
        for (const nickname of nicknames) {
            await fetch(`/api/neighbors/add?targetNickname=${nickname}`, {
                method: "POST"
            });
        }
        showCustomAlert("이웃 추가 완료!");
        fetchNeighbors(); // 목록 갱신
    } catch (err) {
        console.error("이웃 추가 실패:", err);
        showCustomAlert("이웃 추가 중 오류가 발생했습니다.");
    }
});