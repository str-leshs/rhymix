document.addEventListener("DOMContentLoaded", async () => {
    const now = new Date();
    const year = now.getMonth() === 0 ? now.getFullYear() - 1 : now.getFullYear();
    const month = now.getMonth() === 0 ? 12 : now.getMonth();

    // 1. 월별 플레이리스트 생성 (nickname 제거됨)
    let createdPlaylist;
    try {
        const createRes = await fetch(`/api/playlists/generate/monthly?year=${year}&month=${month}`, {
            method: "POST"
        });

        if (!createRes.ok) {
            alert("⚠️ 플레이리스트 생성 실패");
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

    // 2. 최신 플레이리스트 조회
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

    // 3. 곡 표시
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
