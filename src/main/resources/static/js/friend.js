document.addEventListener('DOMContentLoaded', loadRecommendedFriends);

// ✅ 추천 이웃 불러오기 (기존 /api/neighbors → /api/recommend-neighbors)
async function loadRecommendedFriends() {
    try {
        const response = await fetch('/api/recommend-neighbors');
        const friends = await response.json();
        renderFriendList(friends);
    } catch (error) {
        console.error("이웃 추천 목록을 불러오는 데 실패했습니다:", error);
    }
}

// ✅ 추천 이웃 리스트 렌더링
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


// ✅ 새로고침 버튼 클릭 시 이웃 다시 불러오기
document.getElementById("refresh-button").addEventListener("click", loadRecommendedFriends);

// ✅ 이웃 신청 기능 (선택된 항목들 POST 전송)
document.getElementById("request-button").addEventListener("click", async () => {
    const selected = [...document.querySelectorAll('input[name="selected"]:checked')]
        .map(cb => cb.value);

    if (selected.length === 0) {
        showCustomAlert("신청할 친구를 선택하세요!");
        return;
    }

    try {
        await fetch('/api/friends/request', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userIds: selected })  // ✅ userIds는 index 기준이므로 서버에서 처리 시 주의
        });

        showCustomAlert("이웃 신청 완료!");
    } catch (error) {
        console.error("이웃 신청 중 오류 발생:", error);
        showCustomAlert("이웃 신청에 실패했습니다.");
    }
});
