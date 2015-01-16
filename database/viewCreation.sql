CREATE VIEW `processed_trans`
AS SELECT
   `pool_transaction`.`id` AS `id`,
   `pool_transaction`.`version` AS `version`,
   `pool_transaction`.`account_id` AS `account_id`,
   `pool_transaction`.`created_at` AS `created_at`,
   `pool_transaction`.`event_key` AS `event_key`,
   `pool_transaction`.`pick` AS `pick`,
   `pool_transaction`.`pick1amount` AS `pick1amount`,
   `pool_transaction`.`pick1num_people` AS `pick1num_people`,
   `pool_transaction`.`pick2amount` AS `pick2amount`,
   `pool_transaction`.`pick2num_people` AS `pick2num_people`,
   `pool_transaction`.`question_id` AS `question_id`,
   `pool_transaction`.`transaction_amount` AS `transaction_amount`,
   `pool_transaction`.`transaction_type` AS `transaction_type`,(case when (`pool_transaction`.`transaction_amount` > `pool_transaction`.`pick2amount`) then 'win' when (`pool_transaction`.`transaction_amount` = `pool_transaction`.`pick2amount`) then 'tie' when (`pool_transaction`.`transaction_amount` < `pool_transaction`.`pick2amount`) then 'loss' end) AS `game_result`,substr(`pool_transaction`.`event_key`,1,12) AS `league`
FROM `pool_transaction` where (`pool_transaction`.`transaction_type` = 1);

CREATE VIEW `processed_trans_wk`
AS SELECT
   `pool_transaction`.`id` AS `id`,
   `pool_transaction`.`version` AS `version`,
   `pool_transaction`.`account_id` AS `account_id`,
   `pool_transaction`.`created_at` AS `created_at`,
   `pool_transaction`.`event_key` AS `event_key`,
   `pool_transaction`.`pick` AS `pick`,
   `pool_transaction`.`pick1amount` AS `pick1amount`,
   `pool_transaction`.`pick1num_people` AS `pick1num_people`,
   `pool_transaction`.`pick2amount` AS `pick2amount`,
   `pool_transaction`.`pick2num_people` AS `pick2num_people`,
   `pool_transaction`.`question_id` AS `question_id`,
   `pool_transaction`.`transaction_amount` AS `transaction_amount`,
   `pool_transaction`.`transaction_type` AS `transaction_type`
FROM `pool_transaction` where ((`pool_transaction`.`transaction_type` = 1) and (week(`pool_transaction`.`created_at`,1) = week(curdate(),1)));

CREATE VIEW `user_league_stats`
AS SELECT
   `processed_trans`.`account_id` AS `account_id`,
   `processed_trans`.`league` AS `league`,
   `processed_trans`.`game_result` AS `game_result`,(sum(`processed_trans`.`transaction_amount`) - sum(`processed_trans`.`pick2amount`)) AS `net_gain`,count(0) AS `num_games`
FROM `processed_trans` group by 1,2,3;

CREATE VIEW `user_net_gain_all`
AS SELECT
   `a`.`id` AS `id`,(sum(`b`.`transaction_amount`) - sum(`b`.`pick2amount`)) AS `net_gain`,
   `a`.`current_balance` AS `current_balance`
FROM (`account` `a` left join `processed_trans` `b` on((`a`.`id` = `b`.`account_id`))) group by 1;

CREATE VIEW `user_net_gain_wk`
AS SELECT
   `a`.`id` AS `id`,(sum(`b`.`transaction_amount`) - sum(`b`.`pick2amount`)) AS `net_gain`,
   `a`.`current_balance` AS `current_balance`
FROM (`account` `a` left join `processed_trans_wk` `b` on((`a`.`id` = `b`.`account_id`))) group by 1;


CREATE VIEW `user_rank_all`
AS SELECT
   `t1`.`id` AS `id`,(case when (`t1`.`net_gain` is not null) then `t1`.`net_gain` else 0 end) AS `net_gain`,
   `t1`.`current_balance` AS `current_balance`
FROM `user_net_gain_all` `t1` order by `net_gain` desc,`t1`.`current_balance` desc,`t1`.`id`;

CREATE VIEW `user_rank_wk`
AS SELECT
   `t1`.`id` AS `id`,(case when (`t1`.`net_gain` is not null) then `t1`.`net_gain` else 0 end) AS `net_gain`,
   `t1`.`current_balance` AS `current_balance`
FROM `user_net_gain_wk` `t1` order by `net_gain` desc,`t1`.`current_balance` desc,`t1`.`id`;