CLI to analyze TFstate and plan files to extract the list of actions on resources
</br>
</br>
TODO: Not all drift can be fixed by updating a resource, sometimes resources need to be recreated. 
</br>
</br>
Pre-Reqs: terraform binary should be in same repo. TF state/plan files should be passed to this CLI.
</br>
</br>
How To?:
</br>
1. You need to generate a TF plan file first, e.g.:
</br>
   terraform plan -out plan_example.bin
</br>
2. Then you call this utility:
</br>
   java -jar terraformfileanalyzer-1.0.0.jar plan_example.bin summary.json
</br>
3. Then you run TF apply with the given plan file
</br>
   terraform apply plan_example.bin
</br>
4. Then you can complete the analysis by calling again this utility to match the actual IDs with teh planned changes
</br>
   java -jar terraformfileanalyzer-1.0.0.jar terraform.tfstate summary.json