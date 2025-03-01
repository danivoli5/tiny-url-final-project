output "cluster_name" {
  description = "Name of the created GKE cluster"
  value       = google_container_cluster.autopilot.name
}

output "endpoint" {
  description = "Cluster endpoint"
  value       = google_container_cluster.autopilot.endpoint
}

output "location" {
  description = "Cluster location"
  value       = google_container_cluster.autopilot.location
}
