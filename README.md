<b>What's terraform-file-analyzer?</b>
</br>
It's a CLI to analyze TFstate and plan files to extract the list of actions on resources
</br>
</br>
<b>Pre-Reqs</b>
</br>
terraform binary should be in same repo. TF state/plan files should be passed to this CLI.
</br>
</br>
<b>How To use it?</b>
</br>
a. You need to generate a TF plan file first, e.g.:
</br>
   terraform plan -out plan_example.bin
</br>
b. Then you call this utility:
</br>
   java -jar terraformfileanalyzer-1.0.0.jar plan_example.bin summary.json
</br>
c. Then you run TF apply with the given plan file
</br>
   terraform apply plan_example.bin
</br>
d. Then you can complete the analysis by calling again this utility to match the actual IDs with teh planned changes
</br>
   java -jar terraformfileanalyzer-1.0.0.jar terraform.tfstate summary.json
</br>
</br>
<b>TODO</b>
</br>
Not all drift can be fixed by updating a resource, sometimes resources need to be recreated. 