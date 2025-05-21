document.addEventListener("DOMContentLoaded", () => {
    loadUserProfile();
    loadTodayMusic();
    loadPlaylist();
    loadComments();
    setupCommentSubmit();
    setupPostModal();  // 날짜 클릭 시 포스트 상세
});


// 1. 사용자 프로필
function loadUserProfile() {
    fetch('/api/users/me')
        .then(res => res.json())
        .then(user => {
            const nickname = user.nickname?.trim();
            document.getElementById('nickname-box').textContent =
                nickname && nickname.length > 0 ? nickname : '@...';


            const profileImage = user.profileImage?.trim();
            document.getElementById('profile-image').src =
                profileImage ? profileImage : 'image/placeholder_circle.png';

            const bioMessage = user.bio?.trim();
            document.getElementById('bio-message').textContent =
                bioMessage ? bioMessage : '블로그 방문을 환영합니다!';

            const tagList = document.getElementById('tag-list');
            tagList.innerHTML = '';

            const genres = user.preferredGenres || [];
            if (genres.length === 0) {
                const span = document.createElement('span');
                span.className = 'tag';
                span.textContent = '#,,,';
                tagList.appendChild(span);
            } else {
                genres.forEach(tag => {
                    const span = document.createElement('span');
                    span.className = 'tag';
                    span.textContent = `#${tag}`;
                    tagList.appendChild(span);
                });
            }
        });
}
// 2. 오늘의 음악
function loadTodayMusic() {
    fetch('/api/posts/today')
        .then(res => res.json())
        .then(post => {
            const track = post.track || {};
            const coverImage = track.coverImage?.trim();

            document.querySelector('.music-card img').src =
                coverImage ? coverImage : 'image/placeholder_album.png';

            const title = track.title?.trim();
            const artist = track.artist?.trim();
            const mood1 = track.mood1?.trim();
            const mood2 = track.mood2?.trim();

            document.querySelector('.music-title-box').textContent =
                title && title.length > 0 ? `🎵 ${title}` : '🎵 music';

            document.querySelector('.music-artist-box').textContent =
                artist && artist.length > 0 ? `🎤 ${artist}` : '🎤 artist';

            document.getElementById('mood-btn-1').textContent =
                mood1 && mood1.length > 0 ? `🌈 ${mood1}` : '🌈 mood1';

            document.getElementById('mood-btn-2').textContent =
                mood2 && mood2.length > 0 ? `🌈 ${mood2}` : '🌈 mood2';
        });
}








// 4. 플레이리스트 불러오기
function loadPlaylist() {
    fetch('/api/playlists/me')
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


// 5. 댓글 불러오기
function loadComments() {
    fetch('/api/posts/today/comments')
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


// 6. 댓글 작성
function setupCommentSubmit() {
    document.getElementById('comment-submit-btn').addEventListener('click', () => {
        const input = document.getElementById('comment-input');
        const text = input.value.trim();
        if (!text) return;

        fetch('/api/posts/today/comments', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ text })
        })
            .then(res => {
                if (res.ok) {
                    input.value = '';
                    loadComments();
                }
            });
    });
}


// 7. 플레이리스트 생성 (예: "내 음악 등록" 버튼 눌렀을 때)
function savePlaylist(title, description, trackIds) {
    fetch('/api/playlists', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title, description, trackIds })
    })
        .then(res => res.json())
        .then(result => {
            alert("플레이리스트 저장 완료!");
            loadPlaylist(); // 새로고침
        });
}


// 8. 날짜 클릭 시 포스트 상세 보기
function setupPostModal() {
    // 만약 모달이 없다면 만들어도 되고, 콘솔 출력용도 가능
}

function openPostModal(postId) {
    fetch(`/api/posts/${postId}`)
        .then(res => res.json())
        .then(post => {
            alert(`📌 ${post.track.title} - ${post.track.artist}\n기분: ${post.mood}\n메모: ${post.content}`);
        });
}
