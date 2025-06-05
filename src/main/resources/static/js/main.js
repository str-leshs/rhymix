document.addEventListener("DOMContentLoaded", () => {
    const nickname = document.getElementById('hidden-nickname')?.value;

    if (!nickname) {
        alert("로그인이 필요합니다.");
        window.location.href = "/login";
        return;
    }

    loadUserProfile();
    loadTodayMusicAndComments(nickname);
    loadPlaylist();
    setupPostModal();
    setupCalendar(nickname);
    loadDiary();
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


// 사용자 프로필 로딩 + 테마 반영
function loadUserProfile() {
    fetch('/api/auth/me')
        .then(res => res.json())
        .then(user => {
            document.getElementById('nickname-box').textContent ='@'+ user.nickname || '@...';
            document.getElementById('profile-image').src = user.profileImage || '/image/placeholder_circle.png';
            document.getElementById('bio-message').textContent = user.bio || '블로그 방문을 환영합니다!';

            const tagList = document.getElementById('tag-list');
            tagList.innerHTML = '';
            (user.preferredGenres || []).forEach(tag => {
                const span = document.createElement('span');
                span.className = 'tag';
                span.textContent = `#${tag}`;
                tagList.appendChild(span);
            });

            applyThemeClass(user); // 테마 적용
        });
}


// 오늘의 음악
function loadTodayMusicAndComments(nickname) {
    fetch('/api/posts/today')
        .then(res => {
            if (!res.ok) throw new Error("추천곡 없음");
            return res.json();
        })
        .then(post => {
            const musicCard = document.querySelector('.music-card');
            document.querySelector('.music-card img').src = post.cover || '/image/placeholder_album.png';
            document.querySelector('.music-title-box').textContent = `🎵 ${post.title}`;
            document.querySelector('.music-artist-box').textContent = `🎤 ${post.artist}`;
            document.getElementById('weather-btn').textContent = post.weather || '';
            document.getElementById('mood-btn').textContent = post.mood || '';
            document.getElementById('music-comment').textContent = post.comment || '';
            musicCard.style.display = "block";

            loadComments(post.id);
            setupCommentSubmit(post.id, nickname);
        })
        .catch(() => {
            const musicCard = document.querySelector('.music-card');
            musicCard.style.display = "none";

            const container = document.getElementById('music-pick');
            if (!document.getElementById('no-post-placeholder')) {
                const placeholder = document.createElement('div');
                placeholder.id = 'no-post-placeholder';
                placeholder.textContent = "아직 오늘의 추천곡을 등록하지 않았어요!";
                placeholder.style.padding = "10px";
                placeholder.style.textAlign = "center";
                placeholder.style.fontSize = "14px";
                placeholder.style.color = "#888";
                container.appendChild(placeholder);
            }
        });
}



// 플레이리스트
function loadPlaylist() {
    fetch(`/api/playlists/me`)
        .then(res => {
            if (!res.ok) throw new Error("플레이리스트 없음");
            return res.json();
        })
        .then(playlist => {
            const title = playlist.title?.trim() || 'playlist.mix';
            document.getElementById('playlist-title').textContent = title;

            const list = document.getElementById('playlist-items');
            list.innerHTML = '';

            (playlist.tracks || []).forEach(track => {
                const li = document.createElement('li');
                li.textContent = `${track.title} – ${track.artist}`;
                list.appendChild(li);
            });
        })
        .catch(err => {
            console.warn("플레이리스트 로드 실패:", err);
            const list = document.getElementById('playlist-items');
            list.innerHTML = '<li>플레이리스트가 없습니다.</li>';
        });
}

// 댓글
// 댓글 목록 조회
function loadComments(postId) {
    fetch(`/api/posts/${postId}/chats`)
        .then(res => res.json())
        .then(chats => {
            const list = document.getElementById('chat-list');
            list.innerHTML = '';
            chats.forEach(chat => {
                const li = document.createElement('li');
                li.textContent = `${chat.userNickname || '익명'}: ${chat.text}`;
                list.appendChild(li);
            });
        });
}


// 댓글 작성
function setupCommentSubmit(postId, userNickname) {
    const input = document.getElementById('chat-input');
    const button = document.getElementById('chat-submit-btn');

    button.addEventListener('click', () => {
        const text = input.value.trim();
        if (!text) return;

        fetch(`/api/posts/${postId}/chat`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ text, userNickname })
        }).then(res => {
            if (res.ok) {
                input.value = '';
                loadComments(postId);
            }
        });
    });
}


// 포스트 상세보기 모달
function setupPostModal() {}

// 캘린더
function setupCalendar(userId) {
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
        events: `/api/calendar/events?userId=${userId}`,    // 각 날짜별 추천곡 커버를 표시
        eventContent: function (arg) {
            const img = document.createElement('img');
            img.src = arg.event.extendedProps.cover;
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
                const res = await fetch(`/api/calendar/date?userId=${userId}&date=${date}`);
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

//다이어리
function loadDiary() {
    fetch("/api/users/me/diary")
        .then(res => {
            console.log("다이어리 응답 상태:", res.status);
            if (!res.ok) throw new Error("다이어리 없음");
            return res.json();
        })
        .then(diary => {
            console.log("📘 diary 로드:", diary);

            document.getElementById("title-input").value = diary.diaryTitle || "";
            document.getElementById("content-input").textContent = diary.diaryContent || "";

            if (diary.diaryImage) {
                const img = document.createElement("img");
                img.src = diary.diaryImage;
                img.alt = "다이어리 이미지";
                img.style.width = "60%";
                img.style.display = "block";
                img.style.margin = "20px auto";
                img.style.borderRadius = "12px";
                const target = document.querySelector(".today-post-box");
                target.appendChild(img);
            }

        })
        .catch(err => console.error("다이어리 로드 실패:", err));
}



function closeDetailModal() {
    document.getElementById("trackDetailModal").style.display = "none";
}

document.getElementById("logout-btn").addEventListener("click", () => {
    document.getElementById("logout-form").submit();
});

