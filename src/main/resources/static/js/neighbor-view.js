document.addEventListener("DOMContentLoaded", async () => {
    const pathParts = location.pathname.split('/');
    const neighborId = pathParts[pathParts.length - 1]; // 이웃 닉네임

    // 로그인한 사용자 닉네임 가져오기
    let currentUserNickname = null;
    try {
        const res = await fetch('/api/users/me/nickname');
        if (!res.ok) throw new Error('로그인 정보를 가져올 수 없습니다.');
        currentUserNickname = await res.text();
    } catch (e) {
        console.warn('댓글 작성 불가: 로그인한 사용자 정보를 불러오지 못했습니다.');
    }

    loadNeighborProfile(neighborId);
    loadNeighborPost(neighborId, currentUserNickname);
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
        selected = "color1";
    }

    console.log("적용할 테마:", selected);
    document.body.classList.add(`theme-${selected}`);
}

function loadNeighborProfile(nickname) {
    fetch(`/api/users/${nickname}`)
        .then(res => res.json())
        .then(user => {
            document.getElementById('nickname-box').textContent ='@'+ user.nickname || '...';
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
            applyThemeClass(user);
        });
}

//이웃의 오늘의 추천곡
function loadNeighborPost(nickname, currentUserNickname) {
    fetch(`/api/posts/today?nickname=${nickname}`)
        .then(res => res.json())
        .then(post => {
            document.querySelector('.music-card img').src = post.coverImage || '/image/placeholder_album.png';
            document.querySelector('.music-title-box').textContent = `🎵 ${post.trackTitle}`;
            document.querySelector('.music-artist-box').textContent = `🎤 ${post.trackArtist}`;
            document.getElementById('weather-btn').textContent = post.weather || '';
            document.getElementById('mood-btn').textContent = post.mood || '';
            document.querySelector('.mood-caption').textContent = post.comment || '';
            document.getElementById('music-comment').textContent = post.comment || '';

            loadNeighborChats(post.postId);
            setupChatForm(post.postId, currentUserNickname);
        })
        .catch(() => {
            document.querySelector('.music-card').innerHTML = "<p>아직 오늘의 포스팅을 하지 않으셨어요!</p>";
        });
}
// 댓글 조회
function loadNeighborChats(postId) {
    fetch(`/api/posts/${postId}/chats`)
        .then(res => res.json())
        .then(chats => {
            const list = document.getElementById('chat-list');
            list.innerHTML = '';
            chats.forEach(chat => {
                const li = document.createElement('li');
                li.textContent = `${chat.userNickname}: ${chat.text}`;
                list.appendChild(li);
            });
        })
        .catch(err => console.error("댓글 불러오기 오류:", err));
}
// 댓글 작성
function setupChatForm(postId, userNickname) {
    const input = document.getElementById('chat-input');
    const button = document.getElementById('chat-submit-btn');

    button.onclick = () => {
        const text = input.value.trim();
        if (!text || !userNickname) return;

        fetch(`/api/posts/${postId}/chat`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                userNickname: userNickname, //로그인 사용자한 사용자가 댓글을 다는 것임
                text: text
            })
        })
            .then(() => {
                input.value = '';
                loadNeighborChats(postId);
            });
    };
}

//이웃의 다이어리
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

// 이웃의 전월 플레이리스트
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

//이웃의 뮤직캘린더
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
                const res = await fetch(`/api/calendar/date?userId=${nickname}&date=${date}`);
                if (!res.ok) return alert("추천곡을 불러올 수 없습니다.");
                const data = await res.json();

                document.getElementById("modalDetailTitle").textContent = data.trackTitle;
                document.getElementById("modalDetailArtist").textContent = data.trackArtist;
                document.getElementById("modalDetailMood").textContent = data.mood || "-";
                document.getElementById("modalDetailWeather").textContent = data.weather || "-";
                document.getElementById("modalDetailComment").textContent = data.comment || "-";
                document.getElementById("modalDetailCover").src = data.coverImage || "/image/default-cover.png";

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

function closeDetailModal() {
    document.getElementById("trackDetailModal").style.display = "none";
}