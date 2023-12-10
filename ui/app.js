const API_URL = "http://localhost:8080/api/v1";
let GameState = undefined;
let User = undefined;
let StompClient = undefined;

document.getElementById("pitsPerPlayerHolder").innerHTML = document.getElementById("pitsPerPlayer").value
document.getElementById("pitsPerPlayer").addEventListener(
    "input", (evt) => {
        document.getElementById("pitsPerPlayerHolder").innerHTML = evt.target.value
    }
)

document.getElementById("stonesPerPitHolder").innerHTML = document.getElementById("stonesPerPit").value
document.getElementById("stonesPerPit").addEventListener(
    "input", (evt) => {
        document.getElementById("stonesPerPitHolder").innerHTML = evt.target.value
    }
)

Array.from(document.getElementsByClassName("toggle-form-link")).forEach(
    (elem) =>
        elem.addEventListener("click", () => {
            let toDisable = document.querySelector(".form-wrapper.active");
            let toEnable = document.querySelector(".form-wrapper:not(.active)");

            toDisable.classList.remove("active");
            toEnable.classList.add("active");
        })
);

document
    .getElementById("registerButton")
    .addEventListener(
        "click",
        onAuthFormSubmit("registrationForm", `${API_URL}/auth/register`)
    );
document
    .getElementById("loginButton")
    .addEventListener(
        "click",
        onAuthFormSubmit("loginForm", `${API_URL}/auth/login`)
    );

function onAuthFormSubmit(formId, url) {
    return () => {
        let formData = new FormData(document.getElementById(formId));
        lockForm(formId);
        fetch(url, {
            method: "POST",
            body: JSON.stringify({
                username: formData.get("username"),
                password: formData.get("password"),
            }),
            headers: {"Content-type": "application/json; charset=UTF-8"},
        })
            .then((response) =>
                response
                    .json()
                    .then((data) => ({status: response.status, body: data}))
            )
            .then((data) => handleAuthResponse(formId, data));
    };
}

function handleAuthResponse(formId, response) {
    console.log(response);
    let code = response.status;
    let body = response.body;
    if (code === 200) {
        console.log("OK");
        resetForm(formId);
        onAuthSuccess(body);
    } else {
        console.log(body);
        let error = body.message;
        resetForm(formId);
        showError(formId, error);
    }
}

function lockForm(formId) {
    document.querySelector(`#${formId} .username`).disabled = true;
    document.querySelector(`#${formId} .password`).disabled = true;
    document.querySelector(`#${formId} + button`).disabled = true;
}

function resetForm(formId) {
    document.getElementById(formId).reset();
    document.querySelector(`#${formId} .error`).innerHTML = "";
    document.querySelector(`#${formId} .username`).disabled = false;
    document.querySelector(`#${formId} .password`).disabled = false;
    document.querySelector(`#${formId} + button`).disabled = false;
}

function showError(formId, error) {
    document.querySelector(`#${formId} .error`).innerHTML = error;
}

function onAuthSuccess(authResponse) {
    User = authResponse;

    document.querySelector(".page.active").classList.remove("active");
    document.querySelector("#menu-page").classList.add("active");
}

document.getElementById("createGameButton").addEventListener("click", () => {
    let formData = new FormData(document.getElementById("createGameForm"));
    fetch(`${API_URL}/games`, {
        method: "POST",
        body: JSON.stringify({
            pitsPerPlayer: formData.get("pitsPerPlayer"),
            stonesPerPit: formData.get("stonesPerPit"),
            isStealingAllowed: formData.get("isStealingAllowed") === "on",
            isMultipleTurnAllowed: formData.get("isMultipleTurnAllowed") === "on",
        }),
        headers: {
            "Content-type": "application/json; charset=UTF-8",
            Authorization: `Bearer ${User.token}`,
        },
    })
        .then((response) => response.json())
        .then((json) => {
            console.log(json);
            GameState = json;

            var gameId = json.id;
            setupSockets(gameId);
            switchToWaitingRoom(gameId);
        });
});

function switchToWaitingRoom(gameId) {
    document.querySelector("#gameIdHolder").innerHTML = gameId;
    document.querySelector(".page.active").classList.remove("active");
    document.querySelector("#waiting-room-page").classList.add("active");
}

document.getElementById("joinGameButton").addEventListener("click", () => {
    let formData = new FormData(document.getElementById("joinGameForm"));
    let gameId = formData.get("gameId");
    console.log(gameId);
    setupSockets(gameId);

    fetch(`${API_URL}/games/${gameId}/join`, {
        method: "POST",
        headers: {
            "Content-type": "application/json; charset=UTF-8",
            Authorization: `Bearer ${User.token}`,
        },
    })
        .then((response) => response.json())
        .then((json) => {
            console.log(json);
        });
});

function setupSockets(gameId) {
    const stompClient = new StompJs.Client({
        brokerURL: `ws://localhost:8080/websocket?access_token=${User.token}`,
        onConnect: () =>
            stompClient.subscribe(`/topic/v1/game-state.${gameId}`, (message) => {
                let json = JSON.parse(message.body);
                updateBoardState(json);
            }),
    });
    stompClient.activate();
    StompClient = stompClient;
}

function updateBoardState(state) {
    if (
        GameState === undefined ||
        (GameState.status === "WAITING_FOR_PLAYERS" && state.status === "ACTIVE")
    ) {
        startGame(state);
    } else {
        updateGame(state);
    }
}

function startGame(state) {
    GameState = state;

    document.querySelector(".page.active").classList.remove("active");
    document.querySelector("#game-page").classList.add("active");

    let board = state.board;

    let players = state.players;
    let numberOfPlayers = players.length;

    let pitsPerPlayer = state.pitsPerPlayer;
    let totalSpaces = (pitsPerPlayer + 1) * numberOfPlayers;

    spaces = [];
    for (i = 0; i < totalSpaces; i++) {
        var div = document.createElement("div");
        spaces.push(div);
    }

    for (i = 0; i < numberOfPlayers; i++) {
        let rowId = `player-${i}-pits`;
        let row = document.getElementById(rowId);

        let player = players[i];
        let spaceRange = player.spaceRange;
        for (j = spaceRange.firstPitIndex; j <= spaceRange.lastPitIndex; j++) {
            console.log("read ", j);
            let pit = spaces[j];
            pit.classList.add("pit");
            pit.addEventListener("click", onTryTurnRequest(j));
            row.appendChild(pit);
        }

        let store = spaces[spaceRange.storeIndex];
        store.classList.add("store");
        let storeHolder = document.getElementById(`player-${i}-store`);
        storeHolder.appendChild(store);
    }

    for (i = 0; i < board.length; i++) {
        spaces[i].innerHTML = board[i];
    }

    GameState.spaces = spaces;
    let player = GameState.players.find((player) => player.userId === User.id);
    let spaceRange = player.spaceRange;

    for (j = spaceRange.firstPitIndex; j <= spaceRange.lastPitIndex; j++) {
        let pit = spaces[j];
        pit.classList.remove("clickable");
        if (GameState.board[j] != 0) {
            pit.classList.add("clickable");
        }
    }
}

function onTryTurnRequest(index) {
    return () => {
        if (board[index] == 0) {
            return;
        }

        StompClient.publish({
            destination: `/app/v1/game.${GameState.id}`,
            body: JSON.stringify({spaceIndex: index}),
        });
    };
}

function updateGame(state) {
    GameState.board = state.board;

    for (i = 0; i < GameState.board.length; i++) {
        spaces[i].innerHTML = GameState.board[i];
    }

    document.querySelector(".player-tag.active").classList.remove("active");
    document.querySelector(`#player-${state.currentPlayerIndex}-tag`).classList.add("active");

    document.querySelector(".storeHolder.active").classList.remove("active");
    document.querySelector(`#player-${state.currentPlayerIndex}-store`).classList.add("active");

    document.querySelector(".pits.active").classList.remove("active");
    document.querySelector(`#player-${state.currentPlayerIndex}-pits`).classList.add("active");

    if (GameState.status != 'FINISHED' && state.status == 'FINISHED') {
        GameState.status = state.status
        GameState.winnerIndex = state.winnerIndex

        document.getElementById(`player-${state.winnerIndex}-winner`).classList.add("active")
    }
}