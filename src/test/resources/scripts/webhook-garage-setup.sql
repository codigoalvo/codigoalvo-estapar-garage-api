INSERT INTO public.sector
(id, code, base_price, max_capacity, open_hour, close_hour, duration_limit_minutes)
VALUES('847b0a78-a20b-49ec-947b-16ab6549a279'::uuid, 'A', 5.00, 2, '00:00:00', '23:59:00', 1440);
INSERT INTO public.sector
(id, code, base_price, max_capacity, open_hour, close_hour, duration_limit_minutes)
VALUES('05592035-f028-4e79-85db-db5a3cc05ac4'::uuid, 'B', 2.50, 2, '08:00:00', '23:59:00', 60);

INSERT INTO public.spot
(id, external_id, sector_id, latitude, longitude, is_occupied)
VALUES('94c2ce3b-7b4e-4b46-b010-e4e37622a99a'::uuid, 2, '847b0a78-a20b-49ec-947b-16ab6549a279'::uuid, -23.561664, -46.655961, false);
INSERT INTO public.spot
(id, external_id, sector_id, latitude, longitude, is_occupied)
VALUES('2e1a78ab-cc70-42d1-b594-4dd1d7e5df29'::uuid, 11, '05592035-f028-4e79-85db-db5a3cc05ac4'::uuid, -23.561484, -46.655781, false);
INSERT INTO public.spot
(id, external_id, sector_id, latitude, longitude, is_occupied)
VALUES('fbc5f9ad-1be6-45fa-b0aa-038073164d16'::uuid, 12, '05592035-f028-4e79-85db-db5a3cc05ac4'::uuid, -23.561464, -46.655761, false);
INSERT INTO public.spot
(id, external_id, sector_id, latitude, longitude, is_occupied)
VALUES('956d46dd-65dd-4d28-85de-a7aeb211379a'::uuid, 1, '847b0a78-a20b-49ec-947b-16ab6549a279'::uuid, -23.561684, -46.655981, false);