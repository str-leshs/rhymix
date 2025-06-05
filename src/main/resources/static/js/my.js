document.addEventListener('DOMContentLoaded', function () {
    const genre1 = document.getElementById('genre1');
    const genre2 = document.getElementById('genre2');
    const phone1 = document.getElementById('phone1');
    const phone2 = document.getElementById('phone2');
    const phone3 = document.getElementById('phone3');
    const saveBtn = document.querySelector('.save-btn');
    const preview = document.getElementById('preview');
    const imageInput = document.getElementById('imageUpload');

    // 저장 버튼 초기 비활성화
    saveBtn.disabled = true;

    // 이미지 미리보기
    imageInput.addEventListener('change', function () {
        const file = this.files[0];
        if (file && file.type.startsWith('image/')) {
            const reader = new FileReader();
            reader.onload = function (e) {
                preview.style.backgroundImage = `url('${e.target.result}')`;
            };
            reader.readAsDataURL(file);
        }
    });

    // 장르 중복 체크
    function checkGenreDuplicate() {
        if (genre1.value !== '선택' && genre1.value === genre2.value) {
            document.getElementById('genre-warning').style.display = 'block';
            genre2.value = '선택';
        } else {
            document.getElementById('genre-warning').style.display = 'none';
        }
    }
    genre1.addEventListener('change', checkGenreDuplicate);
    genre2.addEventListener('change', checkGenreDuplicate);

    // 전화번호 유효성 검사 및 저장 버튼 활성화
    function validatePhoneAndToggleSave() {
        const valid = phone1.value.trim() !== '' && phone2.value.trim() !== '' && phone3.value.trim() !== '';
        saveBtn.disabled = !valid;
    }

    [phone1, phone2, phone3].forEach(input => {
        input.addEventListener('input', validatePhoneAndToggleSave);
    });

    validatePhoneAndToggleSave(); // 페이지 로드 시 한 번 실행

    // 저장 버튼 클릭 이벤트
    saveBtn.addEventListener('click', function () {
        const nickname = document.getElementById('userId').value;
        const email = document.getElementById('email').value;
        const phone = `${phone1.value}-${phone2.value}-${phone3.value}`;
        const bio = document.getElementById('bio').value;
        const profileImage = preview.style.backgroundImage.replace(/^url\(["']?/, '').replace(/["']?\)$/, '');

        // 장르 정규화 후 저장
        const preferredGenres = [genre1.value, genre2.value]
            .filter(g => g !== '선택')
            .map(g => g.toLowerCase().replace(/[\s\-]/g, ''));

        axios.put(`/api/users/${nickname}`, {
            email, phone, bio, profileImage, preferredGenres
        }).then(() => {
            Swal.fire({
                icon: 'success',
                title: '프로필 저장 완료!',
                text: '변경 사항이 저장되었습니다.',
                confirmButtonText: '확인',
                confirmButtonColor: '#f2c94c',
                background: '#fffde7',
                color: '#5c4100'
            }).then(() => {
                window.location.href = "/main";
            });
        }).catch(err => {
            console.error("저장 실패:", err);
            Swal.fire({
                icon: 'error',
                title: '저장 실패!',
                text: '프로필 저장 중 오류가 발생했습니다.',
                confirmButtonColor: '#f2c94c',
                background: '#fffde7',
                color: '#5c4100'
            });
        });
    });

});

