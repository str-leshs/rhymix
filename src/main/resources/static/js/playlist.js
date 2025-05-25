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


    submitBtn.addEventListener("click", () => {
        const selected = themeSelect.value;
        if (!selected) {
            alert("기분 또는 날씨를 선택해주세요!");
            return;
        }
        const encoded = encodeURIComponent(selected);
        window.location.href = `/playlist/theme?type=${encoded}`;
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


