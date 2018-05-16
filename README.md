# HostInfoChecker
Create a service that each minute login to Linux node by SSH, check RAM, Disk Space and CPU load, put these metrics in any DB. If any of this metrics reach 90% or more - service must report this info by Email. Service also must alert by email if any of metrics are increasing 15 min successively and finally metrics value is over 70%. Please use Java.
