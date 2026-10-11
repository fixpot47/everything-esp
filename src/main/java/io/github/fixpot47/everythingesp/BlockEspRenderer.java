package io.github.fixpot47.everythingesp;

// Adapted from FabricMC Fabric Docs rendering example (CC BY-NC-SA 4.0).
// https://docs.fabricmc.net/develop/rendering/world

import java.util.Optional;
import java.util.OptionalDouble;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

import java.util.List;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class BlockEspRenderer implements ClientModInitializer {
	// #region custom_pipelines_define_pipeline
	private static final RenderPipeline FILLED_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(Identifier.fromNamespaceAndPath("everythingesp", "pipeline/through_walls"))
			.withDepthStencilState(Optional.empty())
			.build()
	);
	// #endregion custom_pipelines_define_pipeline
	// #region custom_pipelines_extraction_phase
	private List<WaypointRenderState> waypointStates = List.of();
	private long lastScan = 0;

	// #endregion custom_pipelines_extraction_phase
	// #region custom_pipelines_drawing_phase
	private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
	private static final Vector3f MODEL_OFFSET = new Vector3f();
	private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
	private static final StagedVertexBuffer stagedBuffer = new StagedVertexBuffer(() -> "Waypoints Buffer", RenderType.SMALL_BUFFER_SIZE);

	// #endregion custom_pipelines_drawing_phase

	@Override
	public void onInitializeClient() {
		LevelExtractionEvents.END_EXTRACTION.register(this::extractWaypoint);
		LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(this::renderAndDrawWaypoint);
	}

	// #region custom_pipelines_extraction_phase
	private void extractWaypoint(LevelExtractionContext context) {
		var client = Minecraft.getInstance();
		if (client.player == null || client.level == null || !TargetList.enabled || TargetList.TARGETS.isEmpty()) {
			this.waypointStates = List.of();
			return;
		}
		long now = System.nanoTime();
		if (now - lastScan < 350_000_000L) return;
		lastScan = now;
		var found = new ArrayList<WaypointRenderState>();
		var p = client.player.blockPosition();
		var cursor = new BlockPos.MutableBlockPos();
		for (int dx = -16; dx <= 16; dx++) {
			for (int dz = -16; dz <= 16; dz++) {
				for (int dy = -16; dy <= 16; dy++) {
					cursor.set(p.getX() + dx, p.getY() + dy, p.getZ() + dz);
					if (!client.level.hasChunkAt(cursor)) continue;
					var state = client.level.getBlockState(cursor);
					if (TargetList.TARGETS.contains(BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString())) {
						found.add(new WaypointRenderState(cursor.getX(), cursor.getY(), cursor.getZ(), 0f, 1f, 0.25f, 0.35f));
						if (found.size() >= 512) {
							this.waypointStates = List.copyOf(found);
							return;
						}
					}
				}
			}
		}
		this.waypointStates = List.copyOf(found);
	}

	// #endregion custom_pipelines_extraction_phase
	// #region custom_pipelines_drawing_phase
	private void renderAndDrawWaypoint(LevelRenderContext context) {
		if (this.waypointStates.isEmpty()) return;
		RenderPipeline renderPipeline = BlockEspRenderer.FILLED_THROUGH_WALLS;
		VertexFormat formatBinding = renderPipeline.getVertexFormatBinding(0);

		assert formatBinding != null;

		PrimitiveTopology primitive = renderPipeline.getPrimitiveTopology();
		StagedVertexBuffer.Draw draw = stagedBuffer.appendDraw(formatBinding, primitive, primitive == PrimitiveTopology.QUADS ? RenderSystem.getProjectionType().vertexSorting() : null);

		this.renderWaypoints(context, draw);

		stagedBuffer.upload();

		StagedVertexBuffer.ExecuteInfo info = stagedBuffer.getExecuteInfo(draw);

		if (info != null) {
			draw(Minecraft.getInstance(), info, renderPipeline);
		}

		stagedBuffer.endFrame();
	}

	private void renderWaypoints(LevelRenderContext context, StagedVertexBuffer.Draw draw) {
		PoseStack matrices = context.poseStack();
		Vec3 camera = context.levelState().cameraRenderState.pos;

		matrices.pushPose();
		matrices.translate(-camera.x, -camera.y, -camera.z);

		final var builder = stagedBuffer.getVertexBuilder(draw);

		for (var point : this.waypointStates) {
			this.renderFilledBox(matrices.last().pose(), builder, point.x(), point.y(), point.z(), point.x() + 1, point.y() + 1, point.z() + 1, point.r(), point.g(), point.b(), point.a());
		}

		matrices.popPose();
	}

	private void renderFilledBox(Matrix4fc positionMatrix, VertexConsumer buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float red, float green, float blue, float alpha) {
		// Front Face
		buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);

		// Back face
		buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);

		// Left face
		buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);

		// Right face
		buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);

		// Top face
		buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);

		// Bottom face
		buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
		buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
	}

	private static void draw(Minecraft client, StagedVertexBuffer.ExecuteInfo info, RenderPipeline pipeline) {
		GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
				.writeTransform(RenderSystem.getModelViewMatrixCopy(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);

		RenderTarget mainTarget = client.gameRenderer.mainRenderTarget();
		GpuTextureView colorTexture = mainTarget.getColorTextureView();

		assert colorTexture != null;

		try (RenderPass renderPass = RenderSystem.getDevice()
				.createCommandEncoder()
				.createRenderPass(() -> "everythingesp" + " example render pipeline rendering", colorTexture, Optional.empty(), mainTarget.getDepthTextureView(), OptionalDouble.empty())) {
			renderPass.setPipeline(pipeline);

			RenderSystem.bindDefaultUniforms(renderPass);
			renderPass.setUniform("DynamicTransforms", dynamicTransforms);

			// Bind texture if applicable:
			// Sampler0 is used for texture inputs in vertices
			// renderPass.bindTexture("Sampler0", textureSetup.texure0(), textureSetup.sampler0());

			renderPass.setVertexBuffer(0, info.vertexBuffer().slice());
			renderPass.setIndexBuffer(info.indexBuffer(), info.indexType());

			// The base vertex is the starting index when we copied the data into the vertex buffer divided by vertex size
			renderPass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
		}
	}
	// #endregion custom_pipelines_drawing_phase

	// #region custom_pipelines_clean_up
	public static void close() {
		stagedBuffer.close();
	}
	// #endregion custom_pipelines_clean_up

	// #region custom_pipelines_extraction_phase
	// Render states should be immutable, thread safe, and fast to create.
	private record WaypointRenderState(int x, int y, int z, float r, float g, float b, float a) { }
	// #endregion custom_pipelines_extraction_phase
}
