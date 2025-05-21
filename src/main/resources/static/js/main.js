document.addEventListener("DOMContentLoaded", () => {
    const nickname = document.getElementById('hidden-nickname')?.value;

    if (!nickname) {
        alert("로그인이 필요합니다.");
        window.location.href = "/login";
        return;
    }

    loadUserProfile();
    loadTodayMusic(nickname);
    loadPlaylist(nickname);
    loadComments(nickname);
    setupCommentSubmit(nickname);
    setupPostModal();
    setupCalendar(nickname);
});

// 1. 사용자 프로필
function loadUserProfile() {
    fetch('/api/auth/me')
        .then(res => res.json())
        .then(user => {
            document.getElementById('nickname-box').textContent = user.nickname || '@...';
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
        });
}

// 2. 오늘의 음악
function loadTodayMusic(userId) {
    fetch('/api/posts/today')
        .then(res => {
            if (!res.ok) throw new Error("추천곡 없음");
            return res.json();
        })
        .then(post => {
            const musicCard = document.querySelector('.music-card');
            const placeholder = document.getElementById('no-post-placeholder');
            musicCard.style.display = "block";
            if (placeholder) placeholder.style.display = "none";

            document.querySelector('.music-card img').src =
                post.cover?.trim() || 'image/placeholder_album.png';

            document.querySelector('.music-title-box').textContent =
                post.title ? `🎵 ${post.title}` : '🎵 music';

            document.querySelector('.music-artist-box').textContent =
                post.artist ? `🎤 ${post.artist}` : '🎤 artist';

            // ✅ mood와 weather는 이모지+텍스트로 저장되어 있으므로 그대로 출력
            document.getElementById('weather-btn').textContent = post.weather || '';
            document.getElementById('mood-btn').textContent = post.mood || '';

        })
        .catch(err => {
            const musicCard = document.querySelector('.music-card');
            const container = document.getElementById('music-pick');

            if (musicCard) musicCard.style.display = "none";

            if (!document.getElementById('no-post-placeholder')) {
                const placeholder = document.createElement('div');
                placeholder.id = 'no-post-placeholder';
                placeholder.textContent = "아직 오늘의 추천곡을 등록하지 않았어요!";
                placeholder.style.padding = "10px";
                placeholder.style.textAlign = "center";
                placeholder.style.fontSize = "14px";
                placeholder.style.color = "#888";
                container.appendChild(placeholder);
            } else {
                document.getElementById('no-post-placeholder').style.display = "block";
            }
        });
}



// 3. 플레이리스트
function loadPlaylist(userId) {
    fetch(`/api/playlists/me?userId=${userId}`)
        .then(res => res.json())
        .then(playlist => {
            const title = playlist.title?.trim() || 'playlist.mix';
            document.getElementById('playlist-title').textContent = title;

            const list = document.getElementById('playlist-items');
            list.innerHTML = '';

            (playlist.trackIds || []).forEach(track => {
                const li = document.createElement('li');
                li.textContent = `${track.title} – ${track.artist}`;
                list.appendChild(li);
            });
        });
}

// 4. 댓글
function loadComments(userId) {
    fetch(`/api/posts/today/comments?userId=${userId}`)
        .then(res => res.json())
        .then(comments => {
            const commentList = document.getElementById('comment-list');
            commentList.innerHTML = '';
            (comments || []).forEach(c => {
                const div = document.createElement('div');
                div.textContent = `${c.userNickname || '익명'}: ${c.text}`;
                commentList.appendChild(div);
            });
        });
}

// 5. 댓글 작성
function setupCommentSubmit(userId) {
    document.getElementById('comment-submit-btn').addEventListener('click', () => {
        const input = document.getElementById('comment-input');
        const text = input.value.trim();
        if (!text) return;

        fetch(`/api/posts/today/comments?userId=${userId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ text })
        })
            .then(res => {
                if (res.ok) {
                    input.value = '';
                    loadComments(userId);
                }
            });
    });
}

// 6. 플레이리스트 저장 예시
function savePlaylist(title, description, trackIds) {
    fetch('/api/playlists', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title, description, trackIds })
    })
        .then(res => res.json())
        .then(() => {
            alert("플레이리스트 저장 완료!");
            loadPlaylist();
        });
}

// 7. 포스트 상세보기 모달
function setupPostModal() {}
function openPostModal(postId) {
    fetch(`/api/posts/${postId}`)
        .then(res => res.json())
        .then(post => {
            alert(`📌 ${post.title} - ${post.artist}\n기분: ${post.mood}\n메모: ${post.comment}`);
        });
}

// 8. 캘린더
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
        events: `/api/calendar/events?userId=${userId}`,
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

// 9. 모달 닫기
function closeDetailModal() {
    document.getElementById("trackDetailModal").style.display = "none";
}

//10. 로그아웃
// Spring Security 방식 로그아웃 처리
document.getElementById("logout-btn").addEventListener("click", () => {
    document.getElementById("logout-form").submit();
});

