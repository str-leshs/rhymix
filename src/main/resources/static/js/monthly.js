document.addEventListener("DOMContentLoaded", async () => {
    const nickname = "lion01"; // 실제 로그인된 사용자 닉네임으로 교체

    // 현재 연도/월 계산
    const now = new Date();
    const year = now.getMonth() === 0 ? now.getFullYear() - 1 : now.getFullYear();
    const month = now.getMonth() === 0 ? 12 : now.getMonth();

    // 월별 플레이리스트 생성
    try {
        const createRes = await fetch(`/api/playlists/generate/monthly?year=${year}&month=${month}&nickname=${nickname}`, {
            method: "POST"
        });

        if (!createRes.ok) {
            alert("⚠️ 플레이리스트 생성 실패");
            window.location.href = "/main";
            return;
        }

        await createRes.json(); // 필요하긴 하지만 여기서는 사용 안함
    } catch (err) {
        console.error("생성 요청 실패:", err);
        alert("서버 오류로 플레이리스트를 생성하지 못했습니다.");
        window.location.href = "/main";
        return;
    }

    // 최신 월별 플레이리스트 가져오기
    try {
        const res = await fetch(`/api/playlists/playlists/me`);
        if (!res.ok) {
            alert("플레이리스트 불러오기 실패");
            return;
        }

        const playlist = await res.json();
        const posts = playlist.tracks;

        if (!posts || posts.length === 0) {
            console.warn("이 플레이리스트에는 등록된 추천곡이 없습니다.");
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

    } catch (err) {
        console.error("플레이리스트 상세 조회 실패:", err);
        alert("플레이리스트를 불러오는 데 실패했습니다.");
    }
});
