# Supabase setup (already applied)

Project: joeclgeodrfasknsofeg (shared with the MI7 website and other apps).

The migration `mi7_app_foundation` is already applied to this project. It is additive only:
- extends `mi7_profiles`, `mi7_deposits`, `mi7_tournaments`
- adds `mi7_transactions`, `mi7_payment_methods`, `mi7_tournament_slots`, `mi7_tournament_rooms`
- functions: `mi7_app_register_profile`, `mi7_approve_deposit`, `mi7_join_tournament`
- private storage bucket `mi7-payment-proofs`

DO NOT run any older generic schema (profiles / wallets / handle_new_user) on this project:
it would overwrite the shop app's `handle_new_user` function and trigger.

Balance lives in `mi7_profiles.bal`, shared with the website.
