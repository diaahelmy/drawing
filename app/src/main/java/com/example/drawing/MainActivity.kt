package com.example.drawing


import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import com.example.drawing.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    val open_gallery: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {

                binding.hi.setImageURI(result.data?.data)

            }

        }


    private val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {

                val ispermitions = it.key
                val isGranted = it.value
                if (isGranted) {

                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                    open_gallery.launch(intent)

                } else {
                    if (ispermitions == android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        Toast.makeText(this, "NOT WORK", Toast.LENGTH_SHORT).show()


                }
            }
        }
    private lateinit var binding: ActivityMainBinding
    private var veiwdraw: draw? = null
    private var mimagebuTToncolorPaint: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        veiwdraw = findViewById(R.id.draw_veiw)
        veiwdraw?.brushsize(10F)
        val linearLayout = binding.dd
        mimagebuTToncolorPaint = linearLayout[0] as ImageButton
        mimagebuTToncolorPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.parller)
        )

        val savebutton = binding.save
        savebutton.setOnClickListener {
            if (isReadSTORAGE())
                lifecycleScope.launch {

                    val draws = binding.drawingVeiwCon

                    savebitmap(readstorege(draws))

                }
        }

        val tbursh: ImageButton = findViewById(R.id.imageButton)
        tbursh.setOnClickListener {
            sizebursh()

        }
        val share = binding.share
        share.setOnClickListener {

            var result = ""
            val f = File(
                externalCacheDir?.absoluteFile.toString()
                        + File.separator + "darwkid" + System.currentTimeMillis() / 1000 + ".png"
            )
            result = f.absolutePath
            if (result.isNotEmpty()) {
                Toast.makeText(this@MainActivity, "Save Successfully", Toast.LENGTH_SHORT)
                    .show()
                share(result)
            } else {
                (Toast.makeText(this@MainActivity, "no work", Toast.LENGTH_SHORT)).show()
            }
        }
        val image = binding.gallery
        image.setOnClickListener {
            Imagefun()
        }
        val undo = binding.undo
        undo.setOnClickListener {
            veiwdraw?.undo()
        }


    }

    private fun isReadSTORAGE(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun sizebursh() {


        val bursh = Dialog(this)
        bursh.setContentView(R.layout.sizebursh)
        bursh.setTitle("BURSH SIZE: ")

        val smallbursh: ImageButton = bursh.findViewById(R.id.ib_small_brush)
        smallbursh.setOnClickListener {
            veiwdraw?.brushsize(5F)
            bursh.dismiss()
        }
        val meduembursh: ImageButton = bursh.findViewById(R.id.ib_mudium_brush)
        meduembursh.setOnClickListener {
            veiwdraw?.brushsize(10F)
            bursh.dismiss()

        }
        val largbursh: ImageButton = bursh.findViewById(R.id.ib_larg_brush)
        largbursh.setOnClickListener {
            veiwdraw?.brushsize(15F)
            bursh.dismiss()

        }
        bursh.show()
    }

    fun color(veiw: View) {
        if (veiw != mimagebuTToncolorPaint) {
            val imageButton = veiw as ImageButton
            val colortag = imageButton.tag.toString()

            veiwdraw?.color(colortag)

            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.parller)
            )
            mimagebuTToncolorPaint?.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.normal)
            )

            mimagebuTToncolorPaint = veiw
        }
    }

    private fun readstorege(veiw: View): Bitmap {
        val result = Bitmap.createBitmap(veiw.width, veiw.height, Bitmap.Config.ARGB_8888)
        val canves = Canvas(result)
        val bgdrawing = veiw.background
        if (bgdrawing != null) {
            bgdrawing.draw(canves)
        } else {
            canves.drawColor(Color.WHITE)
        }
        veiw.draw(canves)
        return result

    }

    private fun Imagefun() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
        else {
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            requestPermission.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))

        }

    }

    private suspend fun savebitmap(mbitmap: Bitmap): String {
        var result = ""
        withContext(Dispatchers.IO) {
            if (mbitmap != null) {
                try {
                    val byte = ByteArrayOutputStream()

                    mbitmap.compress(Bitmap.CompressFormat.PNG, 90, byte)
                    val f = File(
                        externalCacheDir?.absoluteFile.toString()
                                + File.separator + "darwkid" + System.currentTimeMillis() / 1000 + ".png"
                    )
                    val fo = FileOutputStream(f)
                    fo.write(byte.toByteArray())
                    fo.close()
                    result = f.absolutePath
                    runOnUiThread {
                        if (result.isNotEmpty()) {
                            Toast.makeText(
                                this@MainActivity,
                                "Save Successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            (Toast.makeText(
                                this@MainActivity,
                                "no work",
                                Toast.LENGTH_SHORT
                            )).show()
                        }
                    }
                } catch (e: Exception) {
                    result = ""
                    e.printStackTrace()

                }


            }

        }
        return result

    }

    private fun share(result: String) {

        MediaScannerConnection.scanFile(this, arrayOf(result), null) { path, uri ->
            val share = Intent()
            val go = binding.drawingVeiwCon
            val x = readstorege(go)
            share.action = Intent.ACTION_SEND
            share.putExtra(Intent.EXTRA_STREAM, uri)

            share.putExtra(Intent.EXTRA_TEXT, x.toString())
            share.type = "imagr/png"
            startActivity(Intent.createChooser(share, "uri"))


        }


    }


}



