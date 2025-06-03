let recommendedFriends = []; // 전체 친구 데이터를 전역에서 보관

document.addEventListener('DOMContentLoaded', loadRecommendedFriends);

// 추천 이웃 불러오기
async function loadRecommendedFriends() {
    try {
        const response = await fetch('/api/neighbors/suggested');
        const friends = await response.json();
        recommendedFriends = friends;
        renderFriendList(friends);
    } catch (error) {
        console.error("추천 이웃 목록을 불러오는 데 실패했습니다:", error);
    }
}

// 추천 이웃 리스트 보여줌
function renderFriendList(friends) {
    const container = document.getElementById("friend-list");
    container.innerHTML = "";

    friends.forEach((friend, index) => {
        const genreTags = (friend.genres || [])
            .map(genre => `<span class="tag"># ${genre}</span>`)
            .join(' ');

        container.innerHTML += `
            <div class="friend-item">
                <label>
                    <input type="checkbox" name="selected" value="${index}">
                </label>
                <img class="profile-img" src="${friend.profileImage || '/image/placeholder_circle.png'}" alt="프로필">
                <span class="nickname">${friend.nickname}</span>
                <div class="tags">
                    ${genreTags}
                </div>
            </div>
        `;
    });
}

// 새로고침
document.getElementById("refresh-button").addEventListener("click", loadRecommendedFriends);

//이웃 신청 기능
document.getElementById("request-button").addEventListener("click", async () => {
    const selectedIndexes = [...document.querySelectorAll('input[name="selected"]:checked')]
        .map(cb => parseInt(cb.value));

    if (selectedIndexes.length === 0) {
        showCustomAlert("신청할 친구를 선택하세요!");
        return;
    }

    const nicknames = selectedIndexes
        .map(index => recommendedFriends[index]?.nickname)
        .filter(Boolean); // null 방지

    try {
        for (const nickname of nicknames) {
            await fetch(`/api/neighbors/add?targetNickname=${nickname}`, {
                method: 'POST'
            });
        }

        showCustomAlert("이웃 추가 완료!");
        loadRecommendedFriends();

    } catch (error) {
        console.error("이웃 추가 중 오류 발생:", error);
        showCustomAlert("이웃 추가에 실패했습니다.");
    }
});

//커스텀 알림창
function showCustomAlert(message) {
    document.getElementById("alert-message").textContent = message;
    document.getElementById("custom-alert").classList.remove("hidden");
}

document.getElementById("alert-close").addEventListener("click", () => {
    document.getElementById("custom-alert").classList.add("hidden");
});
