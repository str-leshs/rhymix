const genre1 = document.getElementById('genre1');
const genre2 = document.getElementById('genre2');
const preview = document.getElementById('preview');

document.getElementById('imageUpload').addEventListener('change', function () {
    const file = this.files[0];
        if (file && file.type.startsWith('image/')) {
        const reader = new FileReader();
        reader.onload = function (e) {
            preview.style.backgroundImage = `url('${e.target.result}')`;
        };
        reader.readAsDataURL(file);
    }
});

genre1.addEventListener('change', checkGenreDuplicate);
genre2.addEventListener('change', checkGenreDuplicate);

function checkGenreDuplicate() {
    if (genre1.value !== '선택' && genre1.value === genre2.value) {
        document.getElementById('genre-warning').style.display = 'block';
        genre2.value = '선택';
    } else {
        document.getElementById('genre-warning').style.display = 'none';
    }
}

function togglePassword() {
    const pw = document.getElementById('password');
    pw.type = pw.type === 'password' ? 'text' : 'password';
}

document.querySelector('.save-btn').addEventListener('click', function (e) {
    const nickname = document.getElementById('userId').value;
    const email = document.getElementById('email').value;
    const phone = `${document.getElementById('phone1').value}-${document.getElementById('phone2').value}-${document.getElementById('phone3').value}`;
    const bio = document.getElementById('bio').value;
    const profileImage = preview.style.backgroundImage.replace(/^url\(["']?/, '').replace(/["']?\)$/, '');
    const preferredGenres = [genre1.value, genre2.value].filter(g => g !== '선택');

    axios.put(`/api/users/${nickname}`, {
        email, phone, bio, profileImage, preferredGenres
    }).then(() => {
        alert("프로필이 저장되었습니다!");
        window.location.href = "/main";
    }).catch(err => {
        console.error("저장 실패:", err);
        alert("저장 중 오류가 발생했습니다.");
    });
});
