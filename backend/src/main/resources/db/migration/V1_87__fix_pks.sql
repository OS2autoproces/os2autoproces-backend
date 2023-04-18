ALTER TABLE kle_forms ADD PRIMARY KEY(kle_code, form_code);  
ALTER TABLE users_ous ADD PRIMARY KEY(user_id, ou_id);

ALTER TABLE process_children ADD PRIMARY KEY(child_id, process_id);
ALTER TABLE process_domains ADD PRIMARY KEY(domain, process_id);
ALTER TABLE process_it_systems ADD PRIMARY KEY(it_system_id, process_id);
ALTER TABLE process_links ADD PRIMARY KEY(link_id, process_id);
ALTER TABLE process_ous ADD PRIMARY KEY(ou_id, process_id);
ALTER TABLE process_services ADD PRIMARY KEY(service_id, process_id);
ALTER TABLE process_technologies ADD PRIMARY KEY(technology_id, process_id);
ALTER TABLE process_users ADD PRIMARY KEY(user_id, process_id);

ALTER TABLE process_services_aud ADD PRIMARY KEY (service_id, process_id, rev);
