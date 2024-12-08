const reviewsContainer = document.querySelector('.reviews-container');
let database = [];

function initReviews() {
    reviewsContainer.innerHTML = ""; // Очищаем контейнер перед перерисовкой
    for (let review of database) {
        const reviewItem = document.createElement('div');
        reviewItem.classList.add('review-item');

        const reviewComment = document.createElement('p');
        reviewComment.textContent = review.comment;

        reviewItem.appendChild(reviewComment);

        if (isAdmin) {
            const deleteButton = document.createElement('button');
            deleteButton.textContent = "Удалить";
            deleteButton.addEventListener('click', () => deleteReview(review.review_id));
            reviewItem.appendChild(deleteButton);
        }

        reviewsContainer.appendChild(reviewItem);
    }
}

function initializeCalendar() {
    getReviews().then(() => {
        initReviews();
    });
}
initializeCalendar();

function getReviews() {
    return new Promise((resolve) => {
        fetch(`/api/review`)
            .then(response => response.json())
            .then(reviews => {
                database = []
                for (let item of reviews) {
                    database.push({
                        review_id: item.review_id,
                        comment: item.comment,
                        client_id: item.client_id
                    })
                }
            console.log("Отзывы: ", reviews);
            resolve();
        });
    })
}

function deleteReview(reviewId) {
    fetch(`/api/review/${reviewId}`, {
        method: 'DELETE'
    })
        .then(response => response.json())
        .then(responseData => {
        initializeCalendar();
        console.log('Загрузка ', responseData);
    })
}
