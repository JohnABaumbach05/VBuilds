# Cloudinary setup for VBuilds (PR #9)

Cloudinary storage is optional for local development. The existing filesystem upload
behavior remains active until **all three** environment variables are present.

Set these values in IntelliJ Run > Edit Configurations > Environment variables
(or later in your hosting provider's secret/environment settings):

- `CLOUDINARY_CLOUD_NAME=wccsllqk`
- `CLOUDINARY_API_KEY=<your Cloudinary API key>`
- `CLOUDINARY_API_SECRET=<your Cloudinary API secret>`

Never commit the API key or secret to Git, paste them into source code, or
share screenshots of the secret.

Restart the Spring Boot application after changing environment variables.

## Manual verification

1. Run Maven Lifecycle > test. Fix any compile or test failures before merging.
2. Log in and upload a new build photo; verify the returned URL begins with
   `https://res.cloudinary.com/wccsllqk/image/upload/`.
3. Save it as a build's main photo, add it to a build gallery, and refresh.
4. Upload a new profile photo and verify it displays after refresh.
5. Confirm older local build and profile images still display.
6. Check that the corresponding Cloudinary assets appear under `vbuilds/builds`
   and `vbuilds/profiles` in the Cloudinary media library.

## Important migration and deployment notes

- Existing local files are **not** migrated by this PR. Keep backups of
  `uploads/builds` and `uploads/profiles`; they will not automatically exist
  on a new hosting server. Migrate these separately before going live.
- Check that the PostgreSQL columns storing build photo paths, main photos,
  and profile pictures can hold full HTTPS URLs (recommend TEXT).
- Deleting or replacing a photo does **not** delete its old Cloudinary asset
  yet. Plan asset lifecycle cleanup before substantial public usage.
- Do not enable production deployment until remaining CSRF and other security
  checks are complete.
