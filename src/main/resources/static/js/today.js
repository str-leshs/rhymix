let selectedTrackId = null;  // ✅ 선택된 Spotify 트랙 ID 저장용

// 모달 열기
document.getElementById("openModalBtn").addEventListener("click", () => {
    document.getElementById("manualInputModal").style.display = "flex";
});

// 모달 닫기 (취소 버튼)
document.getElementById("cancelTrackBtn").addEventListener("click", () => {
    document.getElementById("manualInputModal").style.display = "none";
});

// Spotify 검색 버튼 클릭
document.getElementById("spotifySearchBtn").addEventListener("click", async () => {
    const query = document.getElementById("spotifySearchInput").value;
    const resultList = document.getElementById("spotifySearchResults");
    resultList.innerHTML = "";

    try {
        const res = await fetch(`/api/spotify/search?query=${encodeURIComponent(query)}`);
        const results = await res.json();

        results.forEach(track => {
            const li = document.createElement("li");
            li.innerHTML = `
          <div>
            <strong>${track.title}</strong> - ${track.artist}
            <img src="${track.albumImageUrl}" width="40" style="vertical-align:middle;">
            <button class="select-track-btn" 
              data-id="${track.trackId}" 
              data-title="${track.title}" 
              data-artist="${track.artist}" 
              data-cover="${track.albumImageUrl}">선택</button>
          </div>
        `;
            resultList.appendChild(li);
        });

        // 트랙 선택 버튼 클릭 시
        document.querySelectorAll(".select-track-btn").forEach(btn => {
            btn.addEventListener("click", (e) => {
                const t = e.target.dataset;
                selectedTrackId = t.id;

                document.getElementById("trackTitle").textContent = t.title;
                document.getElementById("trackArtist").textContent = t.artist;
                document.getElementById("trackCover").src = t.cover;

                document.getElementById("manualInputModal").style.display = "none";
            });
        });

    } catch (err) {
        console.error("검색 실패:", err);
        alert("검색 중 오류 발생");
    }
});

// 저장 버튼 클릭
document.getElementById("saveBtn").addEventListener("click", async () => {
    try {
        const userRes = await fetch("/api/auth/me");
        if (!userRes.ok) {
            alert("로그인이 필요합니다.");
            return;
        }

        const user = await userRes.json();
        const userId = user.nickname;

        if (!selectedTrackId) {
            alert("🎵 먼저 곡을 검색하고 선택해주세요.");
            return;
        }

        const moodSelect = document.getElementById("mood");
        const weatherSelect = document.getElementById("weather");
        const comment = document.getElementById("comment").value;

        const mood = moodSelect.options[moodSelect.selectedIndex].text;
        const weather = weatherSelect.options[weatherSelect.selectedIndex].text;

        const postData = {
            userId: userId,
            trackId: selectedTrackId,
            mood: mood,
            weather: weather,
            comment: comment
        };

        // 이미 오늘 등록한 추천곡이 있는지 확인
        const checkRes = await fetch(`/api/posts/today?userId=${userId}`);
        if (checkRes.ok) {
            const confirmUpdate = confirm("오늘의 추천곡이 이미 등록되어 있습니다. 수정하시겠습니까?");
            if (!confirmUpdate) return;
        }

        // 저장 요청
        const response = await fetch("/api/posts", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(postData)
        });

        if (response.ok) {
            alert("오늘의 추천곡이 등록되었습니다!");
            window.location.href = "/main";
        } else {
            alert("저장에 실패했습니다.");
        }

    } catch (error) {
        console.error("오류 발생:", error);
        alert("문제가 발생했습니다.");
    }
});