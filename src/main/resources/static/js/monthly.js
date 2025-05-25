document.addEventListener("DOMContentLoaded", async () => {
    // URL 파라미터에서 연도와 월 추출
    const urlParams = new URLSearchParams(window.location.search);
    const year = parseInt(urlParams.get("year"), 10);
    const month = parseInt(urlParams.get("month"), 10);

    if (!year || !month) {
        document.getElementById("current-month-label").textContent = "잘못된 날짜입니다.";
        return;
    }

    // 월 정보 표시
    document.getElementById("current-month-label").textContent = `${year}년 ${month}월`;

    // 월별 플레이리스트 생성 요청
    let createdPlaylist;
    try {
        const createRes = await fetch(`/api/playlists/generate/monthly?year=${year}&month=${month}`, {
            method: "POST"
        });

        if (!createRes.ok) {
            alert("플레이리스트 생성 실패");
            window.location.href = "/main";
            return;
        }

        createdPlaylist = await createRes.json();
    } catch (err) {
        console.error("생성 요청 실패:", err);
        alert("서버 오류로 플레이리스트를 생성하지 못했습니다.");
        window.location.href = "/main";
        return;
    }

    // 플레이리스트 상세 조회
    let playlist;
    try {
        const res = await fetch(`/api/playlists/me`);
        if (!res.ok) throw new Error("상세 조회 실패");

        playlist = await res.json();
    } catch (err) {
        console.error("플레이리스트 조회 실패:", err);
        return;
    }

    const posts = playlist.tracks;
    if (!posts || posts.length === 0) {
        console.warn("등록된 추천곡이 없습니다.");
        return;
    }

    const trackGrid = document.getElementById("track-grid");
    const titleEl = document.getElementById("display-title");
    const artistEl = document.getElementById("display-artist");
    const coverEl = document.getElementById("display-cover");

    const showTrack = (track) => {
        titleEl.textContent = track.title;
        artistEl.textContent = track.artist;
        coverEl.src = track.cover;
    };

    showTrack(posts[0]);

    posts.forEach((track, i) => {
        const div = document.createElement("div");
        div.className = "track-item";
        div.innerHTML = `<span>${(i + 1).toString().padStart(2, '0')}</span> ${track.title}<br />-${track.artist}`;
        div.addEventListener("click", () => showTrack(track));
        trackGrid.appendChild(div);
    });
});
