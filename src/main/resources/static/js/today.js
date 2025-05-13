// 모달 열기
// document.getElementById("searchBtn").addEventListener("click", () => {
//     document.getElementById("manualInputModal").style.display = "flex";
// });
document.getElementById("openModalBtn").addEventListener("click", () => {
    document.getElementById("manualInputModal").style.display = "flex";
});


// 모달 확인 버튼
document.getElementById("confirmTrackBtn").addEventListener("click", () => {
    const title = document.getElementById("modalTrackTitle").value;
    const artist = document.getElementById("modalTrackArtist").value;
    const cover = document.getElementById("modalTrackCover").value;

    document.getElementById("trackTitle").textContent = title;
    document.getElementById("trackArtist").textContent = artist;
    document.getElementById("trackCover").src = cover || "/image/default-cover.png";

    document.getElementById("manualInputModal").style.display = "none";
});

// 모달 취소 버튼
document.getElementById("cancelTrackBtn").addEventListener("click", () => {
    document.getElementById("manualInputModal").style.display = "none";
});

document.getElementById("saveBtn").addEventListener("click", async () => {
    const postData = {
        userId: "lion01",
        title: document.getElementById("trackTitle").textContent,
        artist: document.getElementById("trackArtist").textContent,
        cover: document.getElementById("trackCover").src,
        comment: document.getElementById("comment").value,
        mood: document.getElementById("mood").value,
        weather: document.getElementById("weather").value
    };

    try {
        const response = await fetch("/api/posts", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(postData)
        });

        if (response.ok) {
            alert("오늘의 추천곡이 등록되었습니다!");
            window.location.reload();
        } else {
            alert("저장에 실패했습니다.");
        }
    } catch (error) {
        console.error("요청 실패:", error);
        alert("요청 중 오류가 발생했습니다.");
    }
});