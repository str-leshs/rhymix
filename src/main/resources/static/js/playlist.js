document.addEventListener("DOMContentLoaded", () => {
    const themeSelect = document.getElementById("theme-select");
    const themeIcon = document.getElementById("theme-icon");
    const submitBtn = document.getElementById("theme-submit");

    themeSelect.addEventListener("change", () => {
        const selected = themeSelect.value;
        if (!selected) {
            themeIcon.textContent = "☀"; // 기본값
        } else {
            const emoji = selected.split(" ")[0]; // 앞부분 이모지만 추출
            themeIcon.textContent = emoji;
        }
    });

    // 테마(기분/날씨) 플레이리스트 모달창
    function showThemePlaylistModal(playlist) {
        const modal = document.getElementById("theme-modal");
        const titleEl = document.getElementById("theme-modal-title");
        const listEl = document.getElementById("theme-track-list");
        const iconEl = document.getElementById("theme-modal-icon");

        const titleText = playlist.title || "테마 Playlist"; // ex) "😊 행복 테마"
        const emoji = titleText.split(" ")[0]; // 이모지 추출
        const label = titleText.substring(2);  // 이모지 이후 텍스트만

        iconEl.textContent = emoji;
        titleEl.textContent = `${label}`; // label에 이미 "행복 테마" 포함됨

        listEl.innerHTML = playlist.tracks.map(t => `${t.title} - ${t.artist}`).join("<br>");
        modal.style.display = "flex";
    }



    submitBtn.addEventListener("click", async () => {
        const selected = themeSelect.value;
        if (!selected) {
            alert("기분 또는 날씨를 선택해주세요!");
            return;
        }

        try {
            const res = await fetch("/api/playlists/generate/theme", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ tag: selected })
            });

            if (!res.ok) {
                const error = await res.json();
                alert(error.message || "테마 플레이리스트 생성 실패");
                return;
            }

            const playlist = await res.json();

            //모달에 표시하거나 redirect
            showThemePlaylistModal(playlist);

        } catch (err) {
            console.error("생성 요청 실패:", err);
            alert("서버 오류로 테마 플레이리스트를 생성하지 못했습니다.");
        }
    });

});


document.addEventListener("DOMContentLoaded", () => {
    const yearSelect = document.getElementById("year-select");
    const monthGrid = document.getElementById("month-grid");
    const currentYear = new Date().getFullYear();

    // 연도 셀렉트 구성
    for (let y = currentYear; y >= currentYear - 5; y--) {
        const opt = document.createElement("option");
        opt.value = y;
        opt.textContent = y;
        yearSelect.appendChild(opt);
    }

    yearSelect.value = currentYear;

    // 월 버튼 생성
    for (let m = 1; m <= 12; m++) {
        const monthBtn = document.createElement("div");
        monthBtn.className = "month-button";

        // 🎧 + 월 텍스트 구성
        monthBtn.innerHTML = `
        <div style="font-size: 24px;">🎧</div>
        <div>${m}월</div>
      `;

        // 클릭 시 해당 월 페이지 이동
        monthBtn.addEventListener("click", () => {
            const year = yearSelect.value;
            window.location.href = `/playlist/monthly?year=${year}&month=${m}`;
        });

        monthGrid.appendChild(monthBtn);
    }

});


