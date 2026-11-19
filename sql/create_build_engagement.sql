BEGIN;

CREATE TABLE IF NOT EXISTS build_likes (
    build_id INTEGER NOT NULL REFERENCES builds(build_id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    PRIMARY KEY (build_id, user_id)
);

CREATE INDEX IF NOT EXISTS build_likes_user_idx ON build_likes (user_id);

CREATE TABLE IF NOT EXISTS build_comments (
    comment_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    build_id INTEGER NOT NULL REFERENCES builds(build_id) ON DELETE CASCADE,
    author_id INTEGER REFERENCES users(user_id) ON DELETE SET NULL,
    comment VARCHAR(1000) NOT NULL CHECK (length(btrim(comment)) > 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS build_comments_build_idx
    ON build_comments (build_id, comment_id DESC);
CREATE INDEX IF NOT EXISTS build_comments_author_idx ON build_comments (author_id);

COMMIT;
