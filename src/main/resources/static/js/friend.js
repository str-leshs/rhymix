document.addEventListener('DOMContentLoaded', loadFriendList);

async function loadFriendList() {
    const friends = [
        {
            id: 1,
            nickname: "음악덕후",
            profileImageUrl: "/image/placeholder_circle.png",
            tag1: "힙합",
            tag2: "밤산책"
        },
        {
            id: 2,
            nickname: "감성러버",
            profileImageUrl: "/image/placeholder_circle.png",
            tag1: "인디",
            tag2: "기록"
        },
        {
            id: 3,
            nickname: "잔잔러",
            profileImageUrl: "/image/placeholder_circle.png",
            tag1: "재즈",
            tag2: "비오는날"
        },
        {
            id: 4,
            nickname: "EDM파",
            profileImageUrl: "/image/placeholder_circle.png",
            tag1: "EDM",
            tag2: "드라이브"
        },
        {
            id: 5,
            nickname: "밴드쟁이",
            profileImageUrl: "/image/placeholder_circle.png",
            tag1: "밴드",
            tag2: "소극장"
        }
    ];
    renderFriendList(friends);
}

function renderFriendList(friends) {
    const container = document.getElementById("friend-list");
    container.innerHTML = "";

    friends.forEach(friend => {
        container.innerHTML += `
      <div class="friend-item">
        <label>
          <input type="checkbox" name="selected" value="${friend.id}">
        </label>
        <img class="profile-img" src="${friend.profileImageUrl}" alt="프로필">
        <span class="nickname">${friend.nickname}</span>
        <div class="tags">
          <span class="tag"># ${friend.tag1}</span>
          <span class="tag"># ${friend.tag2}</span>
        </div>
      </div>
    `;
    });
}

document.getElementById("refresh-button").addEventListener("click", loadFriendList);

document.getElementById("request-button").addEventListener("click", async () => {
    const selected = [...document.querySelectorAll('input[name="selected"]:checked')]
        .map(cb => cb.value);

    if (selected.length === 0) {
        alert("신청할 친구를 선택하세요!");
        return;
    }

    await fetch('/api/friends/request', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userIds: selected })
    });

    alert("이웃 신청 완료!");
});
