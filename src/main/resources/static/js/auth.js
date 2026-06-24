const AUTH_TOKEN_KEY = 'token';
const AUTH_ID_KEY = 'loginId';

function saveAuth(token, id) {
    localStorage.setItem(AUTH_TOKEN_KEY, token);
    localStorage.setItem(AUTH_ID_KEY, id);
}

function getToken() {
    return localStorage.getItem(AUTH_TOKEN_KEY);
}

function getLoginId() {
    return localStorage.getItem(AUTH_ID_KEY);
}

function isLoggedIn() {
    return getToken() !== null;
}

function logout() {
    localStorage.removeItem(AUTH_TOKEN_KEY);
    localStorage.removeItem(AUTH_ID_KEY);
    location.href = '/index.html';
}

// 보호된 API 호출 시 $.ajax({ headers: authHeader(), ... }) 형태로 사용
function authHeader() {
    const token = getToken();
    return token ? { 'Authorization': 'Bearer ' + token } : {};
}

// 페이지 우상단에 로그인 상태를 표시. id="authNav" 엘리먼트가 있는 페이지에서 호출
function renderAuthNav() {
    const $nav = $('#authNav');
    if ($nav.length === 0) {
        return;
    }

    if (isLoggedIn()) {
        $nav.html(
            '<span class="auth-nav-user">' + getLoginId() + '님</span>' +
            '<button id="logoutBtn" type="button" class="btn btn-outline-secondary btn-sm rounded-pill">로그아웃</button>'
        );
        $('#logoutBtn').on('click', logout);
    } else {
        $nav.html(
            '<a href="/login.html" class="btn btn-outline-primary btn-sm rounded-pill">로그인</a>' +
            '<a href="/signup.html" class="btn btn-primary btn-sm rounded-pill">회원가입</a>'
        );
    }
}
