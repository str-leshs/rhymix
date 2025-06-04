document.addEventListener("DOMContentLoaded", () => {
    const pathParts = location.pathname.split('/');
    const neighborId = pathParts[pathParts.length - 1]; // /neighbor/{nickname} 형태

    loadNeighborProfile(neighborId);
    loadNeighborPost(neighborId);
    loadNeighborDiary(neighborId);
    loadNeighborPlaylist(neighborId);
    setupNeighborCalendar(neighborId);
});

// 사용자 테마 적용 함수
function applyThemeClass(user) {
    const existing = [...document.body.classList].find(c => c.startsWith("theme-color"));
    if (existing) document.body.classList.remove(existing);

    let selected = user.selectedTheme;
    if (!selected || selected.trim() === "") {
        console.warn("선택된 테마가 없습니다. 기본 테마 'color1' 적용.");
        selected = "color1"; // 기본 테마 지정
    }

    console.log("적용할 테마:", selected);
    document.body.classList.add(`theme-${selected}`);
}

// 1. 이웃 정보
function loadNeighborProfile(nickname) {
    fetch(`/api/users/${nickname}`)
        .then(res => res.json())
        .then(user => {
            document.getElementById('nickname-box').textContent = user.nickname || '...';
            document.getElementById('profile-image').src = user.profileImage || '/image/placeholder_circle.png';
            document.getElementById('bio-message').textContent = user.bio || '';
            const tagList = document.getElementById('tag-list');
            tagList.innerHTML = '';
            (user.preferredGenres || []).forEach(tag => {
                const span = document.createElement('span');
                span.className = 'tag';
                span.textContent = `#${tag}`;
                tagList.appendChild(span);
            });

            applyThemeClass(user); // 기존 main.js와 동일한 테마 적용 함수
        });
}

// 2. 오늘의 곡
function loadNeighborPost(nickname) {
    fetch(`/api/posts/today?nickname=${nickname}`)
        .then(res => res.json())
        .then(post => {
            document.querySelector('.music-card img').src = post.cover || '/image/placeholder_album.png';
            document.querySelector('.music-title-box').textContent = `🎵 ${post.title}`;
            document.querySelector('.music-artist-box').textContent = `🎤 ${post.artist}`;
            document.getElementById('weather-btn').textContent = post.weather || '';
            document.getElementById('mood-btn').textContent = post.mood || '';
            document.querySelector('.mood-caption').textContent = post.comment || '';
        })
        .catch(() => {
            document.querySelector('.music-card').innerHTML = "<p>오늘의 추천곡이 없습니다.</p>";
        });
}

// 3. 일기
function loadNeighborDiary(nickname) {
    fetch(`/api/users/${nickname}/diary`)
        .then(res => res.json())
        .then(diary => {
            document.getElementById("title-input").value = diary.diaryTitle || '';
            document.getElementById("content-input").textContent = diary.diaryContent || '';
            if (diary.diaryImage) {
                const img = document.createElement('img');
                img.src = diary.diaryImage;
                img.alt = "이웃 일기 이미지";
                img.style.width = "60%";
                img.style.display = "block";
                img.style.margin = "20px auto";
                img.style.borderRadius = "12px";
                document.querySelector(".today-post-box").appendChild(img);
            }
        });
}

// 4. 전월 플레이리스트
function loadNeighborPlaylist(nickname) {
    fetch(`/api/playlists/monthly?nickname=${nickname}`)
        .then(res => res.json())
        .then(playlist => {
            document.getElementById('playlist-title').textContent = playlist.title || '@.mix';
            const list = document.getElementById('playlist-items');
            list.innerHTML = '';
            (playlist.tracks || []).forEach(track => {
                const li = document.createElement('li');
                li.textContent = `${track.title} – ${track.artist}`;
                list.appendChild(li);
            });
        });
}

// 5. 달력
function setupNeighborCalendar(nickname) {
    const calendarEl = document.getElementById('calendar');
    const titleEl = document.getElementById('calendar-title');
    const prevBtn = document.getElementById('prev-btn');
    const nextBtn = document.getElementById('next-btn');
    if (!calendarEl || !titleEl || !prevBtn || !nextBtn) return;

    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        headerToolbar: false,
        fixedWeekCount: true,
        dayMaxEventRows: 1,
        events: `/api/calendar/events?userId=${nickname}`,
        eventContent: function (arg) {
            const img = document.createElement('img');
            img.src = arg.event.extendedProps.cover || '/image/placeholder_album.png';
            img.className = 'cover-thumb';
            return { domNodes: [img] };
        },
        datesSet: function () {
            const currentDate = calendar.getDate();
            const year = currentDate.getFullYear();
            const month = currentDate.getMonth() + 1;
            titleEl.textContent = `${year}년 ${month}월`;
        },
        eventClick: async function (info) {
            const date = info.event.startStr;
            try {
                const res = await fetch(`/api/calendar/date?userId=${nickname}&date=${date}`); // ✅ 수정
                if (!res.ok) return alert("추천곡을 불러올 수 없습니다.");
                const data = await res.json();

                document.getElementById("modalDetailTitle").textContent = data.title;
                document.getElementById("modalDetailArtist").textContent = data.artist;
                document.getElementById("modalDetailMood").textContent = data.mood || "-";
                document.getElementById("modalDetailWeather").textContent = data.weather || "-";
                document.getElementById("modalDetailComment").textContent = data.comment || "-";
                document.getElementById("modalDetailCover").src = data.cover || "/image/default-cover.png";

                document.getElementById("trackDetailModal").style.display = "flex";
            } catch (e) {
                console.error(e);
                alert("오류가 발생했습니다.");
            }
        }
    });

    calendar.render();
    prevBtn.addEventListener('click', () => calendar.prev());
    nextBtn.addEventListener('click', () => calendar.next());
}

