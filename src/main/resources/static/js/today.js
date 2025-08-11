let selectedTrackId = null;  // 선택된 Spotify 트랙 ID 저장
let isComposing = false;     // 한글/IME 조합 입력 중 여부

// 모달 열기
document.getElementById("openModalBtn").addEventListener("click", () => {
    document.getElementById("manualInputModal").style.display = "flex";
    // 모달 열릴 때 검색창에 포커스(선택사항)
    setTimeout(() => document.getElementById("spotifySearchInput").focus(), 0);
});

// 모달 닫기 (취소 버튼)
document.getElementById("cancelTrackBtn").addEventListener("click", () => {
    document.getElementById("manualInputModal").style.display = "none";
});

// 검색 로직 -> 함수로 분리
async function spotifySearch() {
    const queryInput = document.getElementById("spotifySearchInput");
    const resultList = document.getElementById("spotifySearchResults");
    const query = (queryInput.value || "").trim();

    resultList.innerHTML = "";
    if (!query) return;

    try {
        const res = await fetch(`/api/spotify/search?query=${encodeURIComponent(query)}`);
        if (!res.ok) throw new Error("search failed");
        const results = await res.json();

        results.forEach(track => {
            const li = document.createElement("li");
            li.className = "spotify-result-item";

            li.innerHTML = `
              <div class="spotify-track-info">
                <img src="${track.albumImageUrl}" class="spotify-track-cover" alt="커버">
                <div class="spotify-track-text">
                  <span class="spotify-track-title">${track.title}</span>
                  <span class="spotify-track-artist">${track.artist}</span>
                </div>
              </div>
              <button class="select-track-btn"
                data-track-id="${track.trackId}"
                data-title="${track.title}"
                data-artist="${track.artist}"
                data-cover="${track.albumImageUrl}">선택</button>
            `;
            resultList.appendChild(li);
        });

        // 선택 버튼 바인딩
        document.querySelectorAll(".select-track-btn").forEach(btn => {
            btn.addEventListener("click", (e) => {
                const t = e.currentTarget.dataset;
                selectedTrackId = t.trackId;

                document.getElementById("trackTitle").textContent = t.title;
                document.getElementById("trackArtist").textContent = t.artist;
                document.getElementById("trackCover").src = t.cover;

                document.getElementById("manualInputModal").style.display = "none";
            });
        });

    } catch (err) {
        console.error("검색 실패:", err);
        alert("검색 중 오류가 발생했습니다.");
    }
}

// 곡검색 버튼 클릭 -> 검색 실행
document.getElementById("spotifySearchBtn").addEventListener("click", spotifySearch);

// 입력창: IME 조합 상태 추적 + Enter 검색
const searchInput = document.getElementById("spotifySearchInput");
searchInput.addEventListener("compositionstart", () => { isComposing = true; });
searchInput.addEventListener("compositionend", () => { isComposing = false; });
searchInput.addEventListener("keydown", (e) => {
    if (e.key === "Enter" && !isComposing) {
        e.preventDefault(); // 폼 submit 등 기본 동작 방지
        spotifySearch();
    }
});

// 저장 버튼 클릭
document.getElementById("saveBtn").addEventListener("click", async () => {
    try {
        if (!selectedTrackId) {
            alert("🎵 먼저 곡을 검색하고 선택해주세요.");
            return;
        }

        // 1. 이미 오늘 곡이 등록되었는지 확인
        const todayResponse = await fetch("/api/posts/today", {
            method: "GET",
            credentials: "include"
        });

        if (todayResponse.ok) {
            // 2. 이미 추천곡이 존재 -> 수정 여부 확인
            const confirmUpdate = confirm("오늘 이미 추천곡을 등록하셨습니다.\n새로운 곡으로 수정하시겠습니까?");
            if (!confirmUpdate) return;
        }

        // 3. 계속 진행
        const moodSelect = document.getElementById("mood");
        const weatherSelect = document.getElementById("weather");
        const comment = document.getElementById("comment").value;

        const mood = moodSelect.options[moodSelect.selectedIndex].text;
        const weather = weatherSelect.options[weatherSelect.selectedIndex].text;

        const postData = {
            trackId: selectedTrackId,
            mood,
            weather,
            comment
        };

        const response = await fetch("/api/posts", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(postData),
            credentials: "include"
        });

        if (response.ok) {
            alert("오늘의 추천곡이 등록되었습니다!");
            window.location.href = "/main";
        } else if (response.status === 401) {
            alert("로그인이 필요합니다.");
            window.location.href = "/login";
        } else {
            alert("저장에 실패했습니다.");
        }

    } catch (error) {
        console.error("오류 발생:", error);
        alert("문제가 발생했습니다.");
    }
});
