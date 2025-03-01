provider "google" {
  project = var.project_id
  region  = var.region
}

resource "google_container_cluster" "autopilot" {
  name     = var.cluster_name
  location = var.region

  enable_autopilot = true

  # Autopilot clusters automatically configure node pools
  # Other configurations like network settings, identity, and security are handled by Google
}
